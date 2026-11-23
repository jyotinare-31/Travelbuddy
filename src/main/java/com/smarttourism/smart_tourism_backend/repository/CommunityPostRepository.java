package com.smarttourism.smart_tourism_backend.repository;

import com.smarttourism.smart_tourism_backend.entity.CommunityPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityPostRepository extends JpaRepository<CommunityPost, Integer> {

    List<CommunityPost> findAllByOrderByCreatedAtDesc();

    List<CommunityPost> findAllByOrderByLikesCountDesc();

    List<CommunityPost> findAllByOrderByRatingDesc();

    List<CommunityPost> findByDestinationIdOrderByCreatedAtDesc(Integer destinationId);

    List<CommunityPost> findByUserUserIdOrderByCreatedAtDesc(Integer userId);

    int countByUserUserId(Integer userId);

    @Query("SELECT p FROM CommunityPost p WHERE " +
           "(LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.destinationName) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY p.createdAt DESC")
    List<CommunityPost> searchPosts(@Param("query") String query);

    @Query("SELECT p FROM CommunityPost p WHERE " +
           "(:destinationId IS NULL OR p.destinationId = :destinationId) AND " +
           "(:query IS NULL OR :query = '' OR " +
           "LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.destinationName) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY " +
           "CASE WHEN :sortBy = 'trending' THEN p.likesCount ELSE 0 END DESC, " +
           "CASE WHEN :sortBy = 'topRated' THEN p.rating ELSE 0 END DESC, " +
           "p.createdAt DESC")
    List<CommunityPost> filterPosts(
            @Param("destinationId") Integer destinationId,
            @Param("query") String query,
            @Param("sortBy") String sortBy
    );
}

