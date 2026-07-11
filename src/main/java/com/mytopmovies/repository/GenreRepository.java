package com.mytopmovies.repository;

import com.mytopmovies.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GenreRepository extends JpaRepository<Genre, UUID> {
    Optional<Genre> findByTmdbGenreId(Long tmdbGenreId);
    Optional<Genre> findByNameIgnoreCase(String name);
}
