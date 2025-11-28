package com.cloudj.backend.dto.out;

import java.time.LocalDateTime;

public record MessageResponse(String message, LocalDateTime timestamp) {
}
