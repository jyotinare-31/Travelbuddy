package com.smarttourism.smart_tourism_backend.repository;

import com.smarttourism.smart_tourism_backend.entity.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Integer> {
    List<PostComment> findByPostPostIdOrderByCreatedAtAsc(Integer postId);
    int countByPostPostId(Integer postId);
}

