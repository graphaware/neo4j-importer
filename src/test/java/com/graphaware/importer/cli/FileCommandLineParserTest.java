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

package com.graphaware.importer.cli;

import com.graphaware.importer.config.FileImportConfig;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit test for {@link BaseCommandLineParser}.
 */
public class FileCommandLineParserTest {

    private CommandLineParser<FileImportConfig> parser;

    @Before
    public void setUp() {
        parser = new CsvCommandLineParser();
    }

    @Test
    public void shouldProduceContext() {
        FileImportConfig context = parser.parseArgs(new String[]{
                "-g", "/tmp/graph",
                "-i", "/tmp/input",
                "-o", "/tmp/output",
                "-r", "neo4j.props"
        });

        assertTrue(context != null);
        assertEquals("/tmp/graph", context.getGraphDir());
        assertEquals("/tmp/input", context.getInputDir());
        assertEquals("/tmp/output", context.getOutputDir());
        assertEquals("neo4j.props", context.getProps());
    }

    @Test
    public void shouldFailWithInvalidArgs() {
        assertNull(parser.parseArgs(new String[]{
                "-g", "/tmp/graph",
                "-i", "/tmp/input",
                "-o", "/tmp/output",
                "-r", "neo4j.props",
                "-m", "invalid"
        }));
    }

    @Test
    public void shouldFailWithInvalidArgs2() {
        assertNull(parser.parseArgs(new String[]{
                "-i", "/tmp/input",
                "-o", "/tmp/output",
                "-p", "neo4j.props"
        }));
    }

    @Test
    public void shouldFailWithInvalidArgs3() {
        assertNull(parser.parseArgs(new String[]{
                "-g", "/tmp/graph",
                "-o", "/tmp/output",
                "-r", "neo4j.props"
        }));
    }

    @Test
    public void shouldFailWithInvalidArgs4() {
        assertNull(parser.parseArgs(new String[]{
                "-g", "/tmp/graph",
                "-i", "/tmp/input",
                "-r", "neo4j.props"
        }));
    }

    @Test
    public void shouldFailWithInvalidArgs5() {
        assertNull(parser.parseArgs(new String[]{
                "-g", "/tmp/graph",
                "-i", "/tmp/input",
                "-o", "/tmp/output"
        }));
    }
}
