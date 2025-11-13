package com.cloudj.backend.controller;

import com.cloudj.backend.dto.request.CompleteMultiPartUpload;
import com.cloudj.backend.service.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@CrossOrigin(
        origins = {"http://localhost:5000", "http://127.0.0.1:5000"},
        allowedHeaders = "*"
)
@RequestMapping("/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/pre-signed-url")
    public ResponseEntity<Map<String, String>> generatePresignedURL(
            @RequestParam("fileName") String fileName,
            @RequestParam("contentType") String contentType
    ) {
        String key = String.valueOf(UUID.randomUUID());
        var url = fileService.generatePresignedPutURL(key, fileName, contentType);
        return ResponseEntity.ok(Map.of("key", key, "url", url));
    }

    @PostMapping("/start-multipart")
    public ResponseEntity<Map<String, String>> startMultipartUpload(
            @RequestParam("fileName") String fileName,
            @RequestParam("contentType") String contentType
    ) {
        String key = String.valueOf(UUID.randomUUID());
        var uploadId = fileService.multiPartUploadID(key, fileName, contentType);
        return ResponseEntity.ok(Map.of("key", key, "id", uploadId));
    }

    @PostMapping("{key}/pre-signed-part")
    public ResponseEntity<Map<String, String>> generatePresignedPart(
            @PathVariable String key,
            @RequestParam String uploadId,
            @RequestParam int partNumber
    ) {
        var url = fileService.multiPartPresignedURL(key, uploadId, partNumber);
        return ResponseEntity.ok(Map.of("key", key, "url", url));
    }

    @PostMapping("{key}/complete-multipart-upload")
    public ResponseEntity<Map<String, String>> completeMultipartUpload(
            @PathVariable String key,
            @RequestBody CompleteMultiPartUpload completeMultiPartUpload
    ) {
        var location = fileService.completeMultiPartUpload(key, completeMultiPartUpload);
        return ResponseEntity.ok(Map.of("key", key, "location", location));
    }
}
