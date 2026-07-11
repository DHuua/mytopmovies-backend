package com.mytopmovies.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "genres")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tmdb_genre_id", nullable = false, unique = true)
    private Long tmdbGenreId;

    @Column(nullable = false, unique = true, length = 100)
    private String name;
}
