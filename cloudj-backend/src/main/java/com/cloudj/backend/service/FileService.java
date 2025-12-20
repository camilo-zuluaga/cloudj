package com.cloudj.backend.service;

import com.cloudj.backend.domain.FileMetadata;
import com.cloudj.backend.domain.User;
import com.cloudj.backend.dto.out.MessageResponse;
import com.cloudj.backend.dto.request.CompletedSingleUpload;
import com.cloudj.backend.repository.FileMetadataRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileMetadataRepository metadataRepository;

    @Transactional
    public MessageResponse<FileMetadata> saveMetadata(User user, CompletedSingleUpload request) {
        FileMetadata metadata = FileMetadata.builder()
                .user(user)
                .s3Key(request.keyName())
                .originalFilename(request.fileName())
                .contentType(request.contentType())
                .fileSize(request.fileSize())
                .uploadedAt(LocalDateTime.now())
                .build();

        metadataRepository.save(metadata);
        return new MessageResponse<>(metadata, LocalDateTime.now());
    }

    public FileMetadata getS3KeyById(Long id) {
        return metadataRepository.findById(id).orElseThrow();
    }

    public List<FileMetadata> getUserFiles(Long id) {
        return metadataRepository.findFilesByUserId(id);
    }

    public void deleteFileById(Long id) {
        metadataRepository.deleteById(id);
    }
}
