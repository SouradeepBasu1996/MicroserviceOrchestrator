package com.brain.studio.microservice_orchestrator.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ZipService {

    private static final Logger logger = LoggerFactory.getLogger(ZipService.class);

    public Path zipProject(Path sourceDirPath) {
        Path zipFilePath = sourceDirPath.getParent().resolve(sourceDirPath.getFileName() + ".zip");

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFilePath.toFile()))) {
            Files.walk(sourceDirPath)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        String zipEntryName = sourceDirPath.relativize(path).toString();
                        try {
                            zipOutputStream.putNextEntry(new ZipEntry(zipEntryName));
                            Files.copy(path, zipOutputStream);
                            zipOutputStream.closeEntry();
                        } catch (IOException e) {
                            logger.error("Error while adding file to ZIP: {}", path, e);
                        }
                    });
        } catch (IOException e) {
            logger.error("Failed to create ZIP file: {}", zipFilePath, e);
        }

        return zipFilePath;
    }
}
