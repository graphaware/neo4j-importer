/*
 * Copyright (c) 2015 GraphAware
 *
 * This file is part of GraphAware Framework.
 *
 * GraphAware Framework is free software: you can redistribute it and/or modify it under the terms
 *  of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
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
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Abstract base-class for {@link DataReader} implementations.
 */
public abstract class BaseDataReader implements DataReader {

    private static final Logger LOG = LoggerFactory.getLogger(BaseDataReader.class);

    protected SimpleDateFormat dateFormat;

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize() {
        this.dateFormat = dateFormat();
    }

    /**
     * Create a date format for date conversions.
     *
     * @return date format. By default, it is "dd/MM/yyyy" in GMT+1. Override for different format.
     */
    protected SimpleDateFormat dateFormat() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        format.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        return format;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long readLong(String columnName) {
        String value = readStringForConversion(columnName);

        if (value == null) {
            return null;
        }

        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            LOG.warn("Value " + value + " in column " + columnName + " is not a long");
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer readInt(String columnName) {
        String value = readStringForConversion(columnName);

        if (value == null) {
            return null;
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            LOG.warn("Value " + value + " in column " + columnName + " is not an int");
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long readDate(String columnName) {
        String value = readStringForConversion(columnName);

        if (value == null) {
            return null;
        }

        try {
            return dateFormat.parse(value).getTime();
        } catch (ParseException e) {
            LOG.warn("Value " + value + " in column " + columnName + " is not a date");
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String readString(String columnName) {
        return doReadString(columnName);
    }

    /**
     * Read a String that is intended to be converted to another type.
     *
     * @param columnName column name.
     * @return the String, or <code>null</code> if the value was <code>null</code>, or an empty String.
     */
    protected final String readStringForConversion(String columnName) {
        String value = doReadString(columnName);

        if (value == null) {
            return null;
        }

        if (StringUtils.isEmpty(value.trim())) {
            return null;
        }

        return value.trim();
    }

    /**
     * Read a String from a column.
     *
     * @param columnName column name.
     * @return String, can be <code>null</code>.
     */
    protected abstract String doReadString(String columnName);
}
