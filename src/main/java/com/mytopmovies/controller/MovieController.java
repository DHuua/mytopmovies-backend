package com.mytopmovies.controller;

import com.mytopmovies.dto.movie.MovieResponse;
import com.mytopmovies.entity.User;
import com.mytopmovies.exception.ApiException;
import com.mytopmovies.repository.UserRepository;
import com.mytopmovies.service.MovieService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/movies")
@RequiredArgsConstructor
@Tag(name = "Movies", description = "Browse movies by category: new, popular, by genre, for friends")
public class MovieController {

    private final MovieService movieService;
    private final UserRepository userRepository;

    @GetMapping("/new")
    public Page<MovieResponse> getNewMovies(@PageableDefault(size = 20) Pageable pageable) {
        return movieService.getNewMovies(pageable);
    }

    @GetMapping("/popular")
    public Page<MovieResponse> getPopularMovies(@PageableDefault(size = 20) Pageable pageable) {
        return movieService.getPopularMovies(pageable);
    }

    @GetMapping("/genre/{genreId}")
    public Page<MovieResponse> getMoviesByGenre(
            @PathVariable UUID genreId,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return movieService.getMoviesByGenre(genreId, pageable);
    }

    // "Best Movies for my Friends" — requires auth, uses ratings of accepted friends
    @GetMapping("/for-friends")
    public Page<MovieResponse> getBestForFriends(
            Authentication authentication,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        UUID userId = resolveUserId(authentication);
        return movieService.getBestForFriends(userId, pageable);
    }

    @GetMapping("/{id}")
    public MovieResponse getMovie(@PathVariable UUID id) {
        return movieService.getMovieById(id);
    }

    private UUID resolveUserId(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.UNAUTHORIZED));
        return user.getId();
    }
}
