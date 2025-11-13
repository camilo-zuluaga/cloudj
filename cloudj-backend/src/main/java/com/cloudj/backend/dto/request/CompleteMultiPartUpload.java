package com.cloudj.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.services.s3.model.CompletedPart;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompleteMultiPartUpload {

    private String key;
    private String uploadId;
    private List<CompletedPartDTO> parts;

    public List<CompletedPart> convertCompletedPartsToAWS() {
        return parts.stream()
                .map(p -> CompletedPart.builder()
                        .partNumber(p.getPartNumber())
                        .eTag(p.getETag())
                        .build())
                .collect(Collectors.toList());
    }
}
