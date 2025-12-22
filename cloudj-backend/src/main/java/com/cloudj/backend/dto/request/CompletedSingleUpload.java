package com.cloudj.backend.dto.request;

public record CompletedSingleUpload(String keyName, String fileName, String contentType, Long fileSize) {
}
