package com.smarttourism.smart_tourism_backend.controller;

import com.smarttourism.smart_tourism_backend.dto.*;
import com.smarttourism.smart_tourism_backend.entity.User;
import com.smarttourism.smart_tourism_backend.service.AuthService;
import com.smarttourism.smart_tourism_backend.service.CommunityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/community")
@CrossOrigin(origins = "*")
public class CommunityController {

    private final CommunityService communityService;
    private final AuthService authService;

    public CommunityController(CommunityService communityService, AuthService authService) {
        this.communityService = communityService;
        this.authService = authService;
    }

    // =========================================================
    // POSTS FEED
    // =========================================================

    @GetMapping("/posts")
    public ResponseEntity<List<PostResponseDto>> getFeed(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "destinationId", required = false) Integer destinationId,
            @RequestParam(value = "sortBy", required = false, defaultValue = "latest") String sortBy,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        User currentUser = authService.getUserFromAuthHeader(authHeader);
        Integer currentUserId = currentUser != null ? currentUser.getUserId() : null;

        List<PostResponseDto> posts = communityService.getFeed(search, destinationId, sortBy, currentUserId);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<?> getPostById(
            @PathVariable("id") Integer postId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            User currentUser = authService.getUserFromAuthHeader(authHeader);
            Integer currentUserId = currentUser != null ? currentUser.getUserId() : null;
            return ResponseEntity.ok(communityService.getPostById(postId, currentUserId));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/posts")
    public ResponseEntity<?> createPost(
            @RequestBody CreatePostRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            User user = authService.requireUser(authHeader);
            PostResponseDto post = communityService.createPost(request, user);
            return ResponseEntity.ok(post);
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/posts/{id}")
    public ResponseEntity<?> updatePost(
            @PathVariable("id") Integer postId,
            @RequestBody UpdatePostRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            User user = authService.requireUser(authHeader);
            PostResponseDto post = communityService.updatePost(postId, request, user);
            return ResponseEntity.ok(post);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<?> deletePost(
            @PathVariable("id") Integer postId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            User user = authService.requireUser(authHeader);
            communityService.deletePost(postId, user);
            return ResponseEntity.ok(Map.of("message", "Post deleted successfully", "postId", postId));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // =========================================================
    // LIKES
    // =========================================================

    @PostMapping("/posts/{id}/like")
    public ResponseEntity<?> toggleLike(
            @PathVariable("id") Integer postId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            User user = authService.requireUser(authHeader);
            Map<String, Object> result = communityService.toggleLike(postId, user);
            return ResponseEntity.ok(result);
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // =========================================================
    // COMMENTS
    // =========================================================

    @GetMapping("/posts/{id}/comments")
    public ResponseEntity<List<CommentResponseDto>> getComments(
            @PathVariable("id") Integer postId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        User currentUser = authService.getUserFromAuthHeader(authHeader);
        Integer currentUserId = currentUser != null ? currentUser.getUserId() : null;
        return ResponseEntity.ok(communityService.getComments(postId, currentUserId));
    }

    @PostMapping("/posts/{id}/comments")
    public ResponseEntity<?> addComment(
            @PathVariable("id") Integer postId,
            @RequestBody CommentRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            User user = authService.requireUser(authHeader);
            CommentResponseDto comment = communityService.addComment(postId, request.getCommentText(), user);
            return ResponseEntity.ok(comment);
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<?> deleteComment(
            @PathVariable("commentId") Integer commentId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            User user = authService.requireUser(authHeader);
            communityService.deleteComment(commentId, user);
            return ResponseEntity.ok(Map.of("message", "Comment deleted successfully", "commentId", commentId));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // =========================================================
    // DESTINATION REVIEWS
    // =========================================================

    @GetMapping("/destinations/{id}/reviews")
    public ResponseEntity<?> getDestinationReviews(
            @PathVariable("id") Integer destinationId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        User currentUser = authService.getUserFromAuthHeader(authHeader);
        Integer currentUserId = currentUser != null ? currentUser.getUserId() : null;
        return ResponseEntity.ok(communityService.getDestinationReviews(destinationId, currentUserId));
    }

    @PostMapping("/destinations/{id}/reviews")
    public ResponseEntity<?> addDestinationReview(
            @PathVariable("id") Integer destinationId,
            @RequestBody CreateReviewRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            User user = authService.requireUser(authHeader);
            DestinationReviewDto review = communityService.addDestinationReview(destinationId, request, user);
            return ResponseEntity.ok(review);
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // =========================================================
    // REPORT POST
    // =========================================================

    @PostMapping("/posts/{id}/report")
    public ResponseEntity<?> reportPost(
            @PathVariable("id") Integer postId,
            @RequestBody(required = false) ReportRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            User user = authService.requireUser(authHeader);
            String reason = (request != null && request.getReason() != null) ? request.getReason() : "Inappropriate content";
            communityService.reportPost(postId, reason, user);
            return ResponseEntity.ok(Map.of("message", "Post reported to moderators. Thank you for keeping the community safe.", "postId", postId));
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // =========================================================
    // USER PROFILE
    // =========================================================

    @GetMapping("/profile")
    public ResponseEntity<?> getCurrentUserProfile(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            User user = authService.requireUser(authHeader);
            UserProfileDto profile = communityService.getUserProfile(user.getUserId(), user.getUserId());
            return ResponseEntity.ok(profile);
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping({"/users/{userId}", "/profile/{userId}"})
    public ResponseEntity<?> getUserProfile(
            @PathVariable("userId") Integer userId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            User currentUser = authService.getUserFromAuthHeader(authHeader);
            Integer currentUserId = currentUser != null ? currentUser.getUserId() : null;
            return ResponseEntity.ok(communityService.getUserProfile(userId, currentUserId));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestBody UserDto dto,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            User user = authService.requireUser(authHeader);
            UserDto updated = communityService.updateUserProfile(user, dto);
            return ResponseEntity.ok(updated);
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }

    // =========================================================
    // DESTINATIONS
    // =========================================================

    @GetMapping("/destinations")
    public ResponseEntity<?> getAllDestinations() {
        return ResponseEntity.ok(communityService.getAllDestinations());
    }
}
