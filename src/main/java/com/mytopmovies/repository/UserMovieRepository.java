package com.mytopmovies.repository;

import com.mytopmovies.entity.UserMovie;
import com.mytopmovies.entity.UserMovieId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserMovieRepository extends JpaRepository<UserMovie, UserMovieId> {
    Optional<UserMovie> findByUser_IdAndMovie_Id(UUID userId, UUID movieId);
    List<UserMovie> findAllByUser_IdAndFavoriteTrue(UUID userId);
    List<UserMovie> findAllByUser_IdAndWatchedTrue(UUID userId);
}
