package com.cloudj.backend.repository;

import com.cloudj.backend.domain.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String refreshToken);

    @Modifying
    @Transactional
    @Query("UPDATE RefreshToken rf SET rf.revoked = true WHERE rf.user.username = :username AND revoked = false")
    void revokeAllByUsername(@Param("username") String username);
}
