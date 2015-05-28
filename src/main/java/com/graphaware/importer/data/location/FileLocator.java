package com.graphaware.importer.data.location;

import com.graphaware.importer.data.Data;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * A {@link DataLocator} that translates data locations to absolute paths to files.
 */
public class FileLocator extends SimpleDataLocator {

    private final String dir;

    /**
     * Construct a new locator.
     *
     * @param dir       directory in which to locate files.
     * @param fileNames map of data to logical file names (i.e. without suffix).
     */
    public FileLocator(String dir, Map<Data, String> fileNames) {
        super(fileNames);
        this.dir = dir;
    }

    /**
     * {@inheritDoc}
     *
     * @return absolute path to a file.
     */
    @Override
    public String locate(Data data) {
        String location = super.locate(data);

        try {
            FileUtils.forceMkdir(new File(dir));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new File(dir + File.separator + location + suffix()).getAbsolutePath();
    }

    /**
     * Get the suffix appended to logical file names.
     *
     * @return suffix. ".csv" by default.
     */
    protected String suffix() {
        return ".csv";
    }
}
