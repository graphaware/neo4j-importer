/*
 * Copyright (c) 2013-2015 GraphAware
 *
 * This file is part of the GraphAware Framework.
 *
 * GraphAware Framework is free software: you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details. You should have received a copy of the
 * GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.graphaware.importer.data.access;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * {@link com.graphaware.importer.data.access.DbDataReader} that uses a queue to temporally decouple reading from the
 * database and processing the records.
 * <p/>
 * One thread fills the queue with records, another thread reads from the queue. The queue is blocking. This means if it
 * is full, the database reading thread will block on insert. If it is empty, calls to {@link #readRecord()} will block.
 */
public abstract class QueueDbDataReader extends DbDataReader {

    private static final Logger LOG = LoggerFactory.getLogger(QueueDbDataReader.class);
    public static final String ROW = "row";

    private volatile BlockingQueue<Map<String, String>> records;
    private volatile Map<String, String> record;

    private volatile boolean noMoreRecords = false;

    /**
     * Construct a new reader.
     *
     * @param dbHost   db host. Must not be <code>null</code> or empty.
     * @param dbPort   db port. Must not be <code>null</code> or empty.
     * @param user     db user. Must not be <code>null</code>.
     * @param password db password. Must not be <code>null</code>.
     */
    public QueueDbDataReader(String dbHost, String dbPort, String user, String password) {
        super(dbHost, dbPort, user, password);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void read(final String query, final String hint) {
        if (records != null) {
            throw new IllegalStateException("Previous reader hasn't been closed");
        }

        LOG.info("Start query: \n" + query);

        if (query.startsWith("alter")) {
            jdbcTemplate.execute(query);
            noMoreRecords = true;
            return;
        }

        records = new ArrayBlockingQueue<>(queueCapacity());

        new Thread(new Runnable() {
            @Override
            public void run() {
                Date d1 = Calendar.getInstance().getTime();

                try {
                    jdbcTemplate.query(query, new ResultSetExtractor<Void>() {
                        @Override
                        public Void extractData(ResultSet rs) throws SQLException, DataAccessException {
                            ResultSetMetaData metaData = rs.getMetaData();
                            int colCount = metaData.getColumnCount();

                            while (rs.next()) {
                                Map<String, String> columns = new HashMap<>();
                                for (int i = 1; i <= colCount; i++) {
                                    columns.put(metaData.getColumnLabel(i), rs.getString(i));
                                }
                                columns.put(ROW, String.valueOf(rs.getRow()));

                                try {
                                    records.offer(columns, 1, TimeUnit.HOURS);
                                } catch (InterruptedException e) {
                                    LOG.warn("Was waiting for more than 1 hour to insert a record for processing, had to drop it");
                                }
                            }

                            return null;
                        }
                    });
                }
                finally {
                    noMoreRecords = true;
                }


                long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(Calendar.getInstance().getTime().getTime() - d1.getTime());

                LOG.info("Finished querying for " + hint + " in " + diffInSeconds + " seconds");
            }
        }, "DB READER - " + hint).start();
    }

    /**
     * Get the capacity of the queue for DB records. Defaults to 100,000.
     * @return queue capacity.
     */
    protected int queueCapacity() {
        return 100_000;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void close() {
        records = null;
        record = null;
        noMoreRecords = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String doReadObject(String columnName) {
        return record.get(columnName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int getRow() {
        return Integer.valueOf(record.get(ROW));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean readRecord() {
        while (!noMoreRecords || !records.isEmpty()) {
            try {
                record = records.poll(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                record = null;
            }

            if (record != null) {
                return true;
            }
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRawRecord() {
        StringBuilder row = new StringBuilder();
        for (String columnName : record.keySet()) {
            row.append(record.get(columnName)).append(";");
        }
        return row.toString();
    }
}
