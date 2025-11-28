package com.cloudj.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Entity
@Table(name = "file_metadata")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FileMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String s3Key;

    @Column(nullable = false)
    private String originalFilename;

    @Column(nullable = false)
    private Long fileSize;

    @Column(nullable = false)
    private LocalDateTime uploadedAt;

    @Column(length = 150, nullable = false)
    private String contentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public FileMetadata(String s3Key, String originalFilename, Long fileSize, LocalDateTime uploadedAt, String contentType, User user) {
        this.s3Key = s3Key;
        this.originalFilename = originalFilename;
        this.fileSize = fileSize;
        this.uploadedAt = uploadedAt;
        this.contentType = contentType;
        this.user = user;
    }
}
