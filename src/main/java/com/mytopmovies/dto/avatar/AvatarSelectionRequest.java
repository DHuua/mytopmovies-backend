package com.mytopmovies.dto.avatar;

import jakarta.validation.constraints.NotBlank;

public record AvatarSelectionRequest(
        @NotBlank String avatarPath
) {
}
