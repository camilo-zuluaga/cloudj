package com.cloudj.backend.service;

import com.cloudj.backend.dto.request.CompleteMultiPartUpload;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.*;

import java.time.Duration;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class S3Service {

    @Value("${aws.bucket.name}")
    private String bucketName;

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    public String generatePresignedPutURL(String key, String fileName, String contentType) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .metadata(Map.of("file-name", fileName))
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5))
                .putObjectRequest(objectRequest)
                .build();

        PresignedPutObjectRequest presignedPutObjectRequest = s3Presigner.presignPutObject(presignRequest);
        return presignedPutObjectRequest.url().toString();
    }

    public String getMultiPartUploadID(String key, String fileName, String contentType) {
        CreateMultipartUploadRequest multiPartUploadRequest = CreateMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .metadata(Map.of("file-name", fileName))
                .build();

        var response = s3Client.createMultipartUpload(multiPartUploadRequest);
        return response.uploadId();
    }

    public String getMultiPartPresignedURL(String key, String uploadId, int partNumber) {
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

    public void abortMultipartUpload(String key, String uploadId) {
        AbortMultipartUploadRequest request = AbortMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(key)
                .uploadId(uploadId)
                .build();

        s3Client.abortMultipartUpload(request);
    }

    public String getPresignedURLViewAndDownload(String key) {
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
}
