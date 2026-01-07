package com.cloudj.backend.dto.request;

public record CompletedUpload(String keyName, String fileName, String contentType, Long fileSize) {
}
