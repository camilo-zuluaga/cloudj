package com.cloudj.backend.dto.request;

public record AbortMultipartUpload(String key, String uploadId) {
}
