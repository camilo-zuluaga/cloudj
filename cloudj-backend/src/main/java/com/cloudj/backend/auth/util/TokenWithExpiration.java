package com.cloudj.backend.auth.util;

import java.util.Date;

public record TokenWithExpiration(String token, Date expirationDate) {
}