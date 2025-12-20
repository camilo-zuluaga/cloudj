package com.cloudj.backend.controller;

import com.cloudj.backend.domain.FileMetadata;
import com.cloudj.backend.dto.out.MessageResponse;
import com.cloudj.backend.dto.request.CompleteMultiPartUpload;
import com.cloudj.backend.dto.request.CompletedSingleUpload;
import com.cloudj.backend.dto.request.CustomUserDetails;
import com.cloudj.backend.dto.request.PresignedPart;
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
        var response = fileService.saveMetadata(customUserDetails.getUser(),
                completedSingleUpload);
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

    @PostMapping("/pre-signed-part")
    public ResponseEntity<Map<String, String>> generatePresignedPart(
            @RequestBody PresignedPart presignedPart
    ) {
        var url = s3Service.getMultiPartPresignedURL(presignedPart.key(),
                presignedPart.uploadId(), presignedPart.currentPart());
        return ResponseEntity.ok(Map.of("key", presignedPart.key(), "url", url));
    }

    @PostMapping("/complete-multipart-upload")
    public ResponseEntity<Map<String, String>> completeMultipartUpload(
            @RequestBody CompleteMultiPartUpload completeMultiPartUpload
    ) {
        var location = s3Service.completeMultiPartUpload(completeMultiPartUpload.getKey(), completeMultiPartUpload);
        return ResponseEntity.ok(Map.of("key", completeMultiPartUpload.getKey(), "location", location));
    }

    @PostMapping("{key}/abort-multipart")
    public ResponseEntity<MessageResponse<String>> abortMultipartUpload(@PathVariable String key,
                                                                        @RequestParam String uploadId) {
        s3Service.abortMultipartUpload(key, uploadId);
        return ResponseEntity.ok(new MessageResponse<>("Multi part aborted", LocalDateTime.now()));
    }

    @GetMapping("/view/{key}")
    public ResponseEntity<Map<String, String>> viewDownloadPresignedURL(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable String key,
            @RequestParam String filename
    ) {
        String keyName = "user_%s/%s".formatted(customUserDetails.getId(), key);
        var url = s3Service.getPresignedURLViewAndDownload(keyName, filename);
        return ResponseEntity.ok(Map.of("url", url));
    }

    private String generateKey(Long userId) {
        return "user_%s/%s".formatted(userId, UUID.randomUUID());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteFile(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long id
    ) {
        var fileMetadata= fileService.getS3KeyById(id);
        s3Service.deleteFile(fileMetadata.getS3Key());
        fileService.deleteFileById(id);
        return ResponseEntity.ok(Map.of("message", "file deleted successfully"));
    }

    @GetMapping()
    public List<FileMetadata> getUserFiles(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return fileService.getUserFiles(customUserDetails.getId());
    }
}
