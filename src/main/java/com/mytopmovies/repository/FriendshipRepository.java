package com.mytopmovies.repository;

import com.mytopmovies.entity.Friendship;
import com.mytopmovies.entity.FriendshipId;
import com.mytopmovies.entity.FriendshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FriendshipRepository extends JpaRepository<Friendship, FriendshipId> {
    Optional<Friendship> findByUser_IdAndFriend_Id(UUID userId, UUID friendId);
    List<Friendship> findAllByUser_IdAndStatus(UUID userId, FriendshipStatus status);
}
