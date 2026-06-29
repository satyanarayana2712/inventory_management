package com.inventory.report;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Exports text reports as CSV files. */
public final class CsvExporter {
    /** Writes content to a CSV file. */
    public Path export(String content, Path path) throws IOException {
        Files.createDirectories(path.getParent());
        return Files.writeString(path, content);
    }
}
