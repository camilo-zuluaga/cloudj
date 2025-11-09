package com.cloudj.backend.application.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class FileChunkService {

    public void saveChunks(MultipartFile multipartFile, Path basePath, String fileId, String chunkIndex) {
        try {
            Path path = Files.createFile(Path.of("%s/%s-%s".formatted(basePath, fileId, chunkIndex)));
            Files.write(path, multipartFile.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
