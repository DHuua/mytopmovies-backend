package com.mytopmovies.repository;

import com.mytopmovies.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface MovieRepository extends JpaRepository<Movie, UUID> {

    Optional<Movie> findByTmdbId(Long tmdbId);

    // Category: New movies
    Page<Movie> findAllByOrderByReleaseDateDesc(Pageable pageable);

    // Category: Popular movies
    Page<Movie> findAllByOrderByPopularityDesc(Pageable pageable);

    // Category: by genre
    @Query("""
            SELECT m FROM Movie m
            JOIN m.genres g
            WHERE g.id = :genreId
            ORDER BY m.popularity DESC
            """)
    Page<Movie> findAllByGenreId(@Param("genreId") UUID genreId, Pageable pageable);

    // Category: Best Movies for my Friends
    // averages ratings your accepted friends gave, ranked highest first,
    // excluding movies you've already marked as watched
    @Query("""
            SELECT um.movie FROM UserMovie um
            WHERE um.user.id IN (
                SELECT f.friend.id FROM Friendship f
                WHERE f.user.id = :userId AND f.status = com.mytopmovies.entity.FriendshipStatus.ACCEPTED
            )
            AND um.rating IS NOT NULL
            AND um.movie.id NOT IN (
                SELECT uw.movie.id FROM UserMovie uw
                WHERE uw.user.id = :userId AND uw.watched = true
            )
            GROUP BY um.movie
            ORDER BY AVG(um.rating) DESC
            """)
    Page<Movie> findTopRatedByFriends(@Param("userId") UUID userId, Pageable pageable);
}
