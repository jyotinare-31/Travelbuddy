package com.smarttourism.smart_tourism_backend.repository;

import com.smarttourism.smart_tourism_backend.entity.PostReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostReportRepository extends JpaRepository<PostReport, Integer> {
    List<PostReport> findByPostPostId(Integer postId);
    boolean existsByPostPostIdAndUserUserId(Integer postId, Integer userId);
}

