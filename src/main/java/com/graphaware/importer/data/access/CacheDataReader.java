package com.graphaware.importer.data.access;

import com.graphaware.importer.cache.Cache;
import org.springframework.util.Assert;

import java.util.Iterator;
import java.util.Map;

/**
 * A {@link com.graphaware.importer.data.access.DataReader} that reads data from a {@link com.graphaware.importer.cache.Cache}.
 */
public class CacheDataReader implements DataReader {

    private final Cache cache;
    private final CacheEntryMapper mapper;
    private Iterator<Map.Entry> entryIterator;
    private Map.Entry entry;
    private int entryNumber = 0;

    /**
     * Create a new reader.
     *
     * @param cache  to read from. Must not be <code>null</code>.
     * @param mapper of cached data to "columns". Must not be <code>null</code>.
     */
    public CacheDataReader(Cache cache, CacheEntryMapper mapper) {
        Assert.notNull(cache);
        Assert.notNull(mapper);

        this.cache = cache;
        this.mapper = mapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize() {
        //no-op
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void read(String connectionString, String hint) {
        if (entryIterator != null || entry != null) {
            throw new IllegalStateException("Previous reader hasn't been closed");
        }
        entryIterator = cache.entrySet().iterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        entryIterator = null;
        entry = null;
        entryNumber = 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long readLong(String columnName) {
        Long value = (Long) mapper.getValue(entry, columnName);

        if (value == null) {
            return null;
        }

        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer readInt(String columnName) {
        Integer value = (Integer) mapper.getValue(entry, columnName);

        if (value == null) {
            return null;
        }

        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long readDate(String columnName) {
        throw new UnsupportedOperationException("Not yet implemented"); //probably never needed
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String readString(String columnName) {
        String value = (String) mapper.getValue(entry, columnName);

        if (value == null) {
            return null;
        }

        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getRow() {
        return entryNumber;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean readRecord() {
        if (!entryIterator.hasNext()) {
            return false;
        }

        entry = entryIterator.next();
        entryNumber++;

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRawRecord() {
        return entry.toString();
    }
}
