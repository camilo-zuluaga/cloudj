package com.cloudj.backend.controller;

import com.cloudj.backend.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/pre-signed-url")
    public ResponseEntity<Map<String, String>> generatePresignedURL(
            @RequestParam("fileExtension") String fileExtension
    ) {
        String keyName = "%s.%s".formatted(UUID.randomUUID(), fileExtension);
        var url = fileService.generatePresignedPutURL(keyName);
        return ResponseEntity.ok(Map.of("url", url, "file", keyName));
    }
}
