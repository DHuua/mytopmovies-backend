package com.mytopmovies.service;

import com.mytopmovies.config.AvatarProperties;
import com.mytopmovies.dto.avatar.AvatarResponse;
import com.mytopmovies.entity.User;
import com.mytopmovies.exception.ApiException;
import com.mytopmovies.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvatarService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/png",
            "image/jpeg",
            "image/jpg",
            "image/webp"
    );

    private static final String PREDEFINED_AVATAR_LOCATION = "classpath:/static/avatars/*";
    private static final String PREDEFINED_AVATAR_URL_PREFIX = "/avatars/";
    private static final String CUSTOM_AVATAR_URL_PREFIX = "/user-avatars/";

    private final UserRepository userRepository;
    private final AvatarProperties avatarProperties;
    private final ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    public List<String> getAvailableAvatars() {
        return loadPredefinedAvatarUrls();
    }

    @Transactional
    public AvatarResponse selectPredefinedAvatar(String avatarPath, String userEmail) {
        List<String> availableAvatars = loadPredefinedAvatarUrls();
        if (!availableAvatars.contains(avatarPath)) {
            throw new ApiException("Avatar path is not one of the predefined avatars", HttpStatus.BAD_REQUEST);
        }

        User user = findUserByEmail(userEmail);
        deleteCustomAvatarIfNeeded(user.getAvatarUrl());
        user.setAvatarUrl(avatarPath);
        User updated = userRepository.save(user);
        return new AvatarResponse(updated.getAvatarUrl());
    }

    @Transactional
    public AvatarResponse uploadCustomAvatar(MultipartFile file, String userEmail) {
        validateAvatarFile(file);

        String extension = determineExtension(file);
        String filename = UUID.randomUUID() + extension;
        Path uploadPath = Paths.get(avatarProperties.getUploadDir()).toAbsolutePath().normalize();

        try {
            Files.createDirectories(uploadPath);
        } catch (IOException ex) {
            throw new ApiException("Failed to prepare avatar upload directory", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Path targetPath = uploadPath.resolve(filename);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new ApiException("Failed to store avatar image", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        User user = findUserByEmail(userEmail);
        deleteCustomAvatarIfNeeded(user.getAvatarUrl());
        user.setAvatarUrl(CUSTOM_AVATAR_URL_PREFIX + filename);
        User updated = userRepository.save(user);
        return new AvatarResponse(updated.getAvatarUrl());
    }

    @Transactional
    public AvatarResponse deleteCurrentAvatar(String userEmail) {
        User user = findUserByEmail(userEmail);
        deleteCustomAvatarIfNeeded(user.getAvatarUrl());
        user.setAvatarUrl(avatarProperties.getDefaultAvatar());
        User updated = userRepository.save(user);
        return new AvatarResponse(updated.getAvatarUrl());
    }

    @Transactional(readOnly = true)
    public AvatarResponse getCurrentAvatar(String userEmail) {
        User user = findUserByEmail(userEmail);
        return new AvatarResponse(user.getAvatarUrl());
    }

    private List<String> loadPredefinedAvatarUrls() {
        try {
            Resource[] resources = resourcePatternResolver.getResources(PREDEFINED_AVATAR_LOCATION);
            return List.of(resources).stream()
                    .filter(Resource::exists)
                    .map(Resource::getFilename)
                    .filter(StringUtils::hasText)
                    .map(filename -> PREDEFINED_AVATAR_URL_PREFIX + filename)
                    .collect(Collectors.toList());
        } catch (IOException ex) {
            throw new ApiException("Unable to load predefined avatars", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.UNAUTHORIZED));
    }

    private void deleteCustomAvatarIfNeeded(String avatarUrl) {
        if (avatarUrl == null || !avatarUrl.startsWith(CUSTOM_AVATAR_URL_PREFIX)) {
            return;
        }

        Path uploadPath = Paths.get(avatarProperties.getUploadDir()).toAbsolutePath().normalize();
        Path filePath = uploadPath.resolve(avatarUrl.substring(CUSTOM_AVATAR_URL_PREFIX.length()));
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException ignored) {
            // If cleanup fails, leave the record update to continue. Old files may be removed later.
        }
    }

    private void validateAvatarFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ApiException("Avatar file must not be empty", HttpStatus.BAD_REQUEST);
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new ApiException("Unsupported avatar file type", HttpStatus.BAD_REQUEST);
        }
    }

    private String determineExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (StringUtils.hasText(originalFilename)) {
            String extension = StringUtils.getFilenameExtension(originalFilename);
            if (StringUtils.hasText(extension)) {
                return extension.startsWith(".") ? extension : "." + extension;
            }
        }

        String contentType = file.getContentType();
        if (contentType == null) {
            throw new ApiException("Avatar file content type is unknown", HttpStatus.BAD_REQUEST);
        }

        return switch (contentType.toLowerCase()) {
            case "image/png" -> ".png";
            case "image/jpeg", "image/jpg" -> ".jpg";
            case "image/webp" -> ".webp";
            default -> throw new ApiException("Unsupported avatar file type", HttpStatus.BAD_REQUEST);
        };
    }
}
