package com.cloudj.backend.repository;

import com.cloudj.backend.domain.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {
    @Query(value = "SELECT * FROM file_metadata WHERE user_id = :id", nativeQuery = true)
    List<FileMetadata> findFilesByUserId(@Param("id") Long id);
}
