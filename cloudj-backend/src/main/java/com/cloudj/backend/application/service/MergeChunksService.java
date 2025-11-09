package com.cloudj.backend.application.service;

import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class MergeChunksService {

    public String mergeChunks(String fileName, String totalChunks, Path pathTest, String fileId) {
        try (FileChannel output = new FileOutputStream(fileName).getChannel()) {
            for (int i = 0; i < Integer.parseInt(totalChunks); i++) {
                Path chunkPath = Path.of("%s/%s-%d".formatted(pathTest, fileId, i)).toAbsolutePath();

                // Copying files is more performant with FileChannel
                FileChannel source = new FileInputStream(String.valueOf(chunkPath)).getChannel();
                source.transferTo(0, source.size(), output);
                source.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // TODO: Better API responses and CLEAN UP of temp directory

        return Paths.get(fileName).toAbsolutePath().toString();
    }
}
