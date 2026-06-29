package com.inventory.report;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Lightweight PDF placeholder exporter for environments without PDF libraries. */
public final class PdfExporter {
    /** Writes a PDF-ready text artifact. */
    public Path export(String content, Path path) throws IOException {
        Files.createDirectories(path.getParent());
        return Files.writeString(path, "PDF EXPORT\n" + content);
    }
}
