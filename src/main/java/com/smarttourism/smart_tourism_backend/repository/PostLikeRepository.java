package com.smarttourism.smart_tourism_backend.repository;

import com.smarttourism.smart_tourism_backend.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Integer> {
    Optional<PostLike> findByPostPostIdAndUserUserId(Integer postId, Integer userId);
    boolean existsByPostPostIdAndUserUserId(Integer postId, Integer userId);
    int countByPostPostId(Integer postId);
    void deleteByPostPostIdAndUserUserId(Integer postId, Integer userId);
}

