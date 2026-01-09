package com.cloudj.backend.service;

import com.cloudj.backend.domain.FileMetadata;
import com.cloudj.backend.domain.User;
import com.cloudj.backend.dto.request.CompletedUpload;
import com.cloudj.backend.dto.response.MessageResponse;
import com.cloudj.backend.repository.FileMetadataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    private FileMetadataRepository metadataRepository;

    @InjectMocks
    private FileService fileService;

    private User testUser;
    private FileMetadata testFileMetadata;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("john_doe")
                .email("john_doe@email.com")
                .password("hashed_password")
                .build();

        testFileMetadata = FileMetadata.builder()
                .id(1L)
                .user(testUser)
                .s3Key("1/abc123")
                .originalFilename("test-file.pdf")
                .contentType("application/pdf")
                .fileSize(1024L)
                .build();
    }

    @Test
    void givenValidUserAndRequest_whenMetadataSaved_thenReturnSavedMetadata() {
        CompletedUpload request = new CompletedUpload(
                "1/abc123", "test-file.pdf", "application/pdf", 1024L
        );

        MessageResponse<FileMetadata> response = fileService.saveMetadata(testUser, request);
        assertThat(response).isNotNull();
        assertEquals("test-file.pdf", response.message().getOriginalFilename());
        assertEquals(testUser, response.message().getUser());
    }

    @Test
    void givenValidId_whenGettingById_thenShouldReturnMetadata() {
        when(metadataRepository.findById(1L)).thenReturn(Optional.of(testFileMetadata));

        FileMetadata result = fileService.getS3KeyById(1L);
        assertThat(result).isNotNull();
        assertEquals(1L, result.getId());
        assertEquals("1/abc123", result.getS3Key());
    }

    @Test
    void givenNonValidId_whenGettingById_thenShouldThrowException() {
        when(metadataRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fileService.getS3KeyById(999L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void givenUserWithFiles_whenGettingUserFiles_thenReturnUserFiles() {
        List<FileMetadata> files = List.of(testFileMetadata);
        when(metadataRepository.findFilesByUserId(1L)).thenReturn(files);

        List<FileMetadata> result = fileService.getUserFiles(1L);

        assertEquals(1, result.size());
        assertEquals("test-file.pdf", result.getFirst().getOriginalFilename());
    }

    @Test
    void givenUserWithNoFiles_whenGettingUserFiles_thenReturnEmptyList() {
        when(metadataRepository.findFilesByUserId(1L)).thenReturn(List.of());

        List<FileMetadata> result = fileService.getUserFiles(1L);

        assertEquals(0, result.size());
    }

    @Test
    void givenFileId_whenDeletingFile_thenDeleteFileCallingRepository() {
        fileService.deleteFileById(1L);
        verify(metadataRepository, times(1)).deleteById(1L);
    }
}