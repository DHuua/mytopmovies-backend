package com.mytopmovies.controller;

import com.mytopmovies.dto.avatar.AvatarResponse;
import com.mytopmovies.dto.avatar.AvatarSelectionRequest;
import com.mytopmovies.service.AvatarService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/avatars")
@RequiredArgsConstructor
@Tag(name = "Avatars", description = "Manage predefined and custom user avatars")
public class AvatarController {

    private final AvatarService avatarService;

    @GetMapping
    public List<String> getAvailableAvatars() {
        return avatarService.getAvailableAvatars();
    }

    @PostMapping("/predefined")
    public AvatarResponse selectPredefinedAvatar(
            @Valid @RequestBody AvatarSelectionRequest request,
            Authentication authentication
    ) {
        return avatarService.selectPredefinedAvatar(request.avatarPath(), authentication.getName());
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AvatarResponse uploadCustomAvatar(
            @RequestPart("avatar") MultipartFile avatar,
            Authentication authentication
    ) {
        return avatarService.uploadCustomAvatar(avatar, authentication.getName());
    }

    @DeleteMapping
    public AvatarResponse deleteCurrentAvatar(Authentication authentication) {
        return avatarService.deleteCurrentAvatar(authentication.getName());
    }
}
