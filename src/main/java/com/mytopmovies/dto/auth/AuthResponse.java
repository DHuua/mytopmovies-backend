package com.mytopmovies.dto.auth;

import java.util.UUID;

public record AuthResponse(
        UUID userId,
        String username,
        String email,
        String avatarUrl,
        String accessToken,
        String refreshToken
) {
}
