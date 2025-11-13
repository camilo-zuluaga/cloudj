package com.cloudj.backend.dto.request;

import software.amazon.awssdk.services.s3.model.CompletedPart;

import java.util.List;
import java.util.stream.Collectors;

// TODO: USE LOMBOK
public class CompleteMultiPartUpload {

    private String key;
    private String uploadId;
    private List<CompletedPartDTO> parts;

    public CompleteMultiPartUpload(String key, String uploadId, List<CompletedPartDTO> parts) {
        this.key = key;
        this.uploadId = uploadId;
        this.parts = parts;
    }

    public List<CompletedPart> convertCompletedPartsToAWS() {
        return parts.stream()
                .map(p -> CompletedPart.builder()
                        .partNumber(p.getPartNumber())
                        .eTag(p.geteTag())
                        .build())
                .collect(Collectors.toList());
    }

    public List<CompletedPartDTO> getParts() {
        return parts;
    }

    public void setParts(List<CompletedPartDTO> parts) {
        this.parts = parts;
    }

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "CompleteMultiPartUpload{" +
               "key='" + key + '\'' +
               ", uploadId='" + uploadId + '\'' +
               ", parts=" + parts +
               '}';
    }
}
