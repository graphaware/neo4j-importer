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
