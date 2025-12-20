package com.cloudj.backend.dto.out;

import java.time.LocalDateTime;

public record MessageResponse<T>(T message, LocalDateTime timestamp) {
}
