package com.cloudj.backend.service;

import com.cloudj.backend.dto.request.CompleteMultiPartUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.*;

import java.time.Duration;
import java.util.Map;

@Service
public class FileService {

    @Value("${aws.bucket.name}")
    private String bucketName;

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Autowired
    public FileService(S3Client s3Client, S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
    }

    public String generatePresignedPutURL(String key, String fileName, String contentType) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .metadata(Map.of("file-name", fileName))
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(2))
                .putObjectRequest(objectRequest)
                .build();

        PresignedPutObjectRequest presignedPutObjectRequest = s3Presigner.presignPutObject(presignRequest);
        return presignedPutObjectRequest.url().toString();
    }

    public String multiPartUploadID(String key, String fileName, String contentType) {
        CreateMultipartUploadRequest multiPartUploadRequest = CreateMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .metadata(Map.of("file-name", fileName))
                .build();

        var response = s3Client.createMultipartUpload(multiPartUploadRequest);
        return response.uploadId();
    }

    public String multiPartPresignedURL(String key, String uploadId, int partNumber) {
        UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                .bucket(bucketName)
                .key(key)
                .uploadId(uploadId)
                .partNumber(partNumber)
                .build();

        UploadPartPresignRequest request = UploadPartPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .uploadPartRequest(uploadPartRequest)
                .build();

        PresignedUploadPartRequest presignedUploadPartRequest = s3Presigner.presignUploadPart(request);

        return presignedUploadPartRequest.url().toString();
    }

    public String completeMultiPartUpload(String key, CompleteMultiPartUpload completeMultiPartUpload) {
        CompletedMultipartUpload completed = CompletedMultipartUpload.builder()
                .parts(completeMultiPartUpload.convertCompletedPartsToAWS())
                .build();

        var response = s3Client.completeMultipartUpload(b -> b
                .bucket(bucketName)
                .key(key)
                .uploadId(completeMultiPartUpload.getUploadId())
                .multipartUpload(completed));

        return response.location();
    }

    public String presignedURLViewAndDownload(String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5))
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest request = s3Presigner.presignGetObject(getObjectPresignRequest);

        return request.url().toString();
    }

    public void deleteFile(String fileName) {
        s3Client.deleteObject(req -> req.bucket(bucketName).key(fileName));
    }
}
