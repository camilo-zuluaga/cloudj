package com.cloudj.backend.dto.response;

import java.time.LocalDateTime;

public record MessageResponse<T>(T message, LocalDateTime timestamp) {
}
