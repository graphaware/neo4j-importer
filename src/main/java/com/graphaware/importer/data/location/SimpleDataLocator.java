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

package com.graphaware.importer.data.location;

import com.graphaware.importer.data.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * A primitive {@link com.graphaware.importer.data.location.DataLocator} that gets the data locations passed in at
 * construction time.
 */
public class SimpleDataLocator implements DataLocator {
    private static final Logger LOG = LoggerFactory.getLogger(SimpleDataLocator.class);

    private final Map<Data, String> locations;

    /**
     * Create a new locator.
     *
     * @param locations data locations.
     */
    public SimpleDataLocator(Map<Data, String> locations) {
        this.locations = locations;
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Performs no checks by default, should be overridden.
     */
    @Override
    public void check() {
        //to be overridden
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Set<Data> allData() {
        return Collections.unmodifiableSet(locations.keySet());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean canLocate(Data data) {
        Assert.notNull(data);

        return locations.containsKey(data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String locate(Data data) {
        LOG.info("Locating " + data + "...");

        if (!canLocate(data)) {
            LOG.error("Cannot locate " + data);
            throw new IllegalStateException("Cannot locate " + data);
        }

        return this.locations.get(data);
    }
}
