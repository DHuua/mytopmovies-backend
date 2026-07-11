package com.mytopmovies.dto.movie;

import com.mytopmovies.entity.Movie;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record MovieResponse(
        UUID id,
        Long tmdbId,
        String title,
        String overview,
        String posterUrl,
        LocalDate releaseDate,
        Double popularity,
        Double voteAverage,
        List<String> genres
) {
    public static MovieResponse from(Movie movie, String tmdbImageBaseUrl) {
        String posterUrl = movie.getPosterPath() != null
                ? tmdbImageBaseUrl + movie.getPosterPath()
                : null;

        Set<String> genreNames = movie.getGenres().stream()
                .map(g -> g.getName())
                .collect(Collectors.toSet());

        return new MovieResponse(
                movie.getId(),
                movie.getTmdbId(),
                movie.getTitle(),
                movie.getOverview(),
                posterUrl,
                movie.getReleaseDate(),
                movie.getPopularity(),
                movie.getVoteAverage(),
                genreNames.stream().toList()
        );
    }
}
