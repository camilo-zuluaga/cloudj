package com.cloudj.backend.auth.security;

import java.util.Date;

public record TokenWithExpiration(String token, Date expirationDate) {
}
