package com.cloudj.backend.dto.request;

public record PresignedPart(String key, String uploadId, int currentPart) {
}
