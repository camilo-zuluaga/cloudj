package com.cloudj.backend.controller;

import com.cloudj.backend.domain.FileMetadata;
import com.cloudj.backend.dto.out.MessageResponse;
import com.cloudj.backend.dto.request.CompleteMultiPartUpload;
import com.cloudj.backend.dto.request.CompletedSingleUpload;
import com.cloudj.backend.dto.request.CustomUserDetails;
import com.cloudj.backend.service.FileService;
import com.cloudj.backend.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@CrossOrigin(
        origins = {"http://localhost:5173", "http://127.0.0.1:5173"},
        allowedHeaders = "*"
)
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final S3Service s3Service;
    private final FileService fileService;

    @PostMapping("/presigned-url")
    public ResponseEntity<Map<String, String>> generatePresignedURL(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam("fileName") String fileName,
            @RequestParam("contentType") String contentType
    ) {
        // Remember s3 is an object storage, so it is not hierarchical
        // to implement some type of "structure", create an empty object simulating the folder
        // then name the file with the folder like folder/file
        Long userId = customUserDetails.getId();
        String key = generateKey(userId);

        var url = s3Service.generatePresignedPutURL(key, fileName, contentType);
        return ResponseEntity.ok(Map.of("key", key, "url", url));
    }

    @PostMapping("/complete-single-upload")
    public ResponseEntity<MessageResponse> completeSingleUploadURL(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody CompletedSingleUpload completedSingleUpload
    ) {
        var response = fileService.saveMetadata(customUserDetails.getUser(), completedSingleUpload);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/start-multipart")
    public ResponseEntity<Map<String, String>> startMultipartUpload(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam("fileName") String fileName,
            @RequestParam("contentType") String contentType
    ) {
        Long userId = customUserDetails.getId();
        String key = generateKey(userId);

        var uploadId = s3Service.getMultiPartUploadID(key, fileName, contentType);
        return ResponseEntity.ok(Map.of("key", key, "id", uploadId));
    }

    @PostMapping("{key}/pre-signed-part")
    public ResponseEntity<Map<String, String>> generatePresignedPart(
            @PathVariable String key,
            @RequestParam String uploadId,
            @RequestParam int partNumber
    ) {
        var url = s3Service.getMultiPartPresignedURL(key, uploadId, partNumber);
        return ResponseEntity.ok(Map.of("key", key, "url", url));
    }

    @PostMapping("{key}/complete-multipart-upload")
    public ResponseEntity<Map<String, String>> completeMultipartUpload(
            @PathVariable String key,
            @RequestBody CompleteMultiPartUpload completeMultiPartUpload
    ) {
        var location = s3Service.completeMultiPartUpload(key, completeMultiPartUpload);
        return ResponseEntity.ok(Map.of("key", key, "location", location));
    }

    @PostMapping("{key}/abort-multipart")
    public ResponseEntity<MessageResponse> abortMultipartUpload(@PathVariable String key,
                                                                @RequestParam String uploadId) {
        s3Service.abortMultipartUpload(key, uploadId);
        return ResponseEntity.ok(new MessageResponse("Multi part aborted", LocalDateTime.now()));
    }

    @GetMapping("/view/{key}")
    public ResponseEntity<Map<String, String>> viewDownloadPresignedURL(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable String key
    ) {
        String keyName = "user_%s/%s".formatted(customUserDetails.getId(), key);
        var url = s3Service.getPresignedURLViewAndDownload(keyName);
        return ResponseEntity.ok(Map.of("url", url));
    }

    private String generateKey(Long userId) {
        return "user_%s/%s".formatted(userId, UUID.randomUUID());
    }

    @GetMapping()
    public List<FileMetadata> getUserFiles(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return fileService.getUserFiles(customUserDetails.getId());
    }
}
