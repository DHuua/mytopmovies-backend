package com.mytopmovies.service;

import com.mytopmovies.dto.movie.MovieResponse;
import com.mytopmovies.entity.Movie;
import com.mytopmovies.exception.ApiException;
import com.mytopmovies.repository.GenreRepository;
import com.mytopmovies.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;

    @Value("${app.tmdb.image-base-url}")
    private String tmdbImageBaseUrl;

    public Page<MovieResponse> getNewMovies(Pageable pageable) {
        return movieRepository.findAllByOrderByReleaseDateDesc(pageable).map(this::toResponse);
    }

    public Page<MovieResponse> getPopularMovies(Pageable pageable) {
        return movieRepository.findAllByOrderByPopularityDesc(pageable).map(this::toResponse);
    }

    public Page<MovieResponse> getMoviesByGenre(UUID genreId, Pageable pageable) {
        if (!genreRepository.existsById(genreId)) {
            throw new ApiException("Genre not found", HttpStatus.NOT_FOUND);
        }
        return movieRepository.findAllByGenreId(genreId, pageable).map(this::toResponse);
    }

    public Page<MovieResponse> getBestForFriends(UUID userId, Pageable pageable) {
        return movieRepository.findTopRatedByFriends(userId, pageable).map(this::toResponse);
    }

    public MovieResponse getMovieById(UUID id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ApiException("Movie not found", HttpStatus.NOT_FOUND));
        return toResponse(movie);
    }

    private MovieResponse toResponse(Movie movie) {
        return MovieResponse.from(movie, tmdbImageBaseUrl);
    }
}
