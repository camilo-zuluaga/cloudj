package com.cloudj.backend.adapter.in.web;

import com.cloudj.backend.application.service.FileChunkService;
import com.cloudj.backend.application.service.MergeChunksService;
import com.cloudj.backend.domain.model.FileChunk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

@RestController
@CrossOrigin(origins = "http://127.0.0.1:5000", allowedHeaders = "*") // we are using a browser!
public class FileUploadController {

    @Value("${filePath}")
    private Path basePath;
    private FileChunkService fileChunkService;
    private MergeChunksService mergeChunksService;

    @Autowired
    public FileUploadController(FileChunkService fileChunkService, MergeChunksService mergeChunksService) {
        this.fileChunkService = fileChunkService;
        this.mergeChunksService = mergeChunksService;
    }

    @PostMapping("/files")
    public ResponseEntity<FileChunk> saveChunks(@RequestPart("chunk") MultipartFile chunk,
                                                @RequestPart("fileId") String fileId,
                                                @RequestPart("chunkIndex") String chunkIndex) {

        fileChunkService.saveChunks(chunk, basePath, fileId, chunkIndex);
        return new ResponseEntity<>(new FileChunk(fileId, chunkIndex), HttpStatus.OK);
    }

    @PostMapping("/merge/{fileId}")
    public ResponseEntity<String> mergeChunks(@PathVariable String fileId,
                                              @RequestPart("totalChunks") String totalChunks,
                                              @RequestPart("fileName") String fileName) {

        mergeChunksService.mergeChunks(fileName, totalChunks, basePath, fileId);

        return new ResponseEntity<>("Done", HttpStatus.OK);
    }
}
