package com.cloudj.backend.controller;

import com.cloudj.backend.domain.FileMetadata;
import com.cloudj.backend.dto.response.*;
import com.cloudj.backend.dto.request.*;
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
    public ResponseEntity<PresignedUrlResponse> generatePresignedURL(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam("fileName") String fileName,
            @RequestParam("contentType") String contentType
    ) {
         /*
         S3 is an object storage, so it is not hierarchical
         to implement some type of "structure", create an empty object simulating the folder
         then name the file with the folder like folder/file
         */

        Long userId = customUserDetails.getId();
        String key = generateKey(userId);

        var url = s3Service.generatePresignedPutURL(key, fileName, contentType);
        return ResponseEntity.ok(new PresignedUrlResponse(key, url));
    }

    @PostMapping("/start-multipart")
    public ResponseEntity<StartMultipartResponse> startMultipartUpload(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam("fileName") String fileName,
            @RequestParam("contentType") String contentType
    ) {
        Long userId = customUserDetails.getId();
        String key = generateKey(userId);

        var uploadId = s3Service.getMultiPartUploadID(key, fileName, contentType);
        return ResponseEntity.ok(new StartMultipartResponse(key, uploadId));
    }

    @PostMapping("/presigned-part")
    public ResponseEntity<PresignedPartResponse> generatePresignedPart(
            @RequestBody PresignedPart presignedPart
    ) {
        var url = s3Service.getMultiPartPresignedURL(presignedPart.key(),
                presignedPart.uploadId(), presignedPart.currentPart());
        return ResponseEntity.ok(new PresignedPartResponse(presignedPart.key(), url));
    }

    @PostMapping("/complete-multipart-upload")
    public ResponseEntity<CompleteMultipartResponse> completeMultipartUpload(
            @RequestBody CompleteMultiPartUpload completeMultiPartUpload
    ) {
        var location = s3Service.completeMultiPartUpload(completeMultiPartUpload.getKey(), completeMultiPartUpload);
        return ResponseEntity.ok(new CompleteMultipartResponse(completeMultiPartUpload.getKey(), location));
    }

    @PostMapping("/abort-multipart")
    public ResponseEntity<MessageResponse<String>> abortMultipartUpload(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody AbortMultipartUpload abortMultipartUpload) {
        s3Service.abortMultipartUpload(abortMultipartUpload.key(), abortMultipartUpload.uploadId());
        return ResponseEntity.ok(new MessageResponse<>("Multi part aborted", LocalDateTime.now()));
    }

    @PostMapping("/complete-upload")
    public ResponseEntity<MessageResponse<FileMetadata>> completeSingleUploadURL(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody CompletedUpload completedUpload
    ) {
        var response = fileService.saveMetadata(customUserDetails.getUser(),
                completedUpload);
        return ResponseEntity.ok(response);
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
    public ResponseEntity<MessageResponse<String>> deleteFile(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long id
    ) {
        var fileMetadata = fileService.getS3KeyById(id);
        s3Service.deleteFile(fileMetadata.getS3Key());
        fileService.deleteFileById(id);
        return ResponseEntity.ok(new MessageResponse<>("File deleted successfully", LocalDateTime.now()));
    }

    @GetMapping()
    public List<FileMetadata> getUserFiles(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return fileService.getUserFiles(customUserDetails.getId());
    }
}
