package com.smarttourism.smart_tourism_backend.service;

import com.smarttourism.smart_tourism_backend.dto.*;
import com.smarttourism.smart_tourism_backend.entity.*;
import com.smarttourism.smart_tourism_backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommunityService {

    private final CommunityPostRepository postRepository;
    private final PostLikeRepository likeRepository;
    private final PostCommentRepository commentRepository;
    private final DestinationReviewRepository reviewRepository;
    private final PostReportRepository reportRepository;
    private final UserRepository userRepository;
    private final TouristPlaceRepository touristPlaceRepository;

    public CommunityService(CommunityPostRepository postRepository,
                            PostLikeRepository likeRepository,
                            PostCommentRepository commentRepository,
                            DestinationReviewRepository reviewRepository,
                            PostReportRepository reportRepository,
                            UserRepository userRepository,
                            TouristPlaceRepository touristPlaceRepository) {
        this.postRepository = postRepository;
        this.likeRepository = likeRepository;
        this.commentRepository = commentRepository;
        this.reviewRepository = reviewRepository;
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.touristPlaceRepository = touristPlaceRepository;
    }

    // =========================================================
    // POSTS FEED
    // =========================================================

    public List<PostResponseDto> getFeed(String search, Integer destinationId, String sortBy, Integer currentUserId) {
        List<CommunityPost> posts;

        String query = (search != null) ? search.trim() : null;
        if (query != null && query.isEmpty()) {
            query = null;
        }

        if (destinationId != null || query != null || (sortBy != null && !sortBy.equalsIgnoreCase("latest"))) {
            posts = postRepository.filterPosts(destinationId, query, sortBy != null ? sortBy : "latest");
        } else {
            posts = postRepository.findAllByOrderByCreatedAtDesc();
        }

        return posts.stream().map(p -> {
            boolean isLiked = currentUserId != null && likeRepository.existsByPostPostIdAndUserUserId(p.getPostId(), currentUserId);
            return PostResponseDto.fromEntity(p, currentUserId, isLiked);
        }).collect(Collectors.toList());
    }

    public PostResponseDto getPostById(Integer postId, Integer currentUserId) {
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post not found with id: " + postId));
        boolean isLiked = currentUserId != null && likeRepository.existsByPostPostIdAndUserUserId(postId, currentUserId);
        return PostResponseDto.fromEntity(post, currentUserId, isLiked);
    }

    @Transactional
    public PostResponseDto createPost(CreatePostRequest req, User user) {
        if (req.getTitle() == null || req.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Post title is required.");
        }
        if (req.getDescription() == null || req.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Post description is required.");
        }

        CommunityPost post = new CommunityPost();
        post.setUser(user);
        post.setTitle(req.getTitle().trim());
        post.setDescription(req.getDescription().trim());
        post.setDestinationId(req.getDestinationId());

        String destName = req.getDestinationName();
        if ((destName == null || destName.trim().isEmpty()) && req.getDestinationId() != null) {
            destName = touristPlaceRepository.findById(req.getDestinationId())
                    .map(TouristPlace::getPlaceName)
                    .orElse("Solapur Destination");
        }
        post.setDestinationName(destName != null ? destName.trim() : "Solapur");

        post.setRating(req.getRating() != null && req.getRating() >= 1 && req.getRating() <= 5 ? req.getRating() : 5);
        post.setImageUrl(req.getImageUrl() != null && !req.getImageUrl().trim().isEmpty() ? req.getImageUrl().trim() : null);
        post.setLikesCount(0);
        post.setCommentsCount(0);
        post.setIsReported(false);

        post = postRepository.save(post);
        return PostResponseDto.fromEntity(post, user.getUserId(), false);
    }

    @Transactional
    public PostResponseDto updatePost(Integer postId, UpdatePostRequest req, User user) {
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post not found with id: " + postId));

        if (!post.getUser().getUserId().equals(user.getUserId())) {
            throw new SecurityException("You are not authorized to edit this post.");
        }

        if (req.getTitle() != null && !req.getTitle().trim().isEmpty()) {
            post.setTitle(req.getTitle().trim());
        }
        if (req.getDescription() != null && !req.getDescription().trim().isEmpty()) {
            post.setDescription(req.getDescription().trim());
        }
        if (req.getDestinationId() != null) {
            post.setDestinationId(req.getDestinationId());
        }
        if (req.getDestinationName() != null && !req.getDestinationName().trim().isEmpty()) {
            post.setDestinationName(req.getDestinationName().trim());
        }
        if (req.getRating() != null && req.getRating() >= 1 && req.getRating() <= 5) {
            post.setRating(req.getRating());
        }
        if (req.getImageUrl() != null) {
            post.setImageUrl(req.getImageUrl().trim().isEmpty() ? null : req.getImageUrl().trim());
        }

        post = postRepository.save(post);
        boolean isLiked = likeRepository.existsByPostPostIdAndUserUserId(postId, user.getUserId());
        return PostResponseDto.fromEntity(post, user.getUserId(), isLiked);
    }

    @Transactional
    public void deletePost(Integer postId, User user) {
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post not found with id: " + postId));

        if (!post.getUser().getUserId().equals(user.getUserId())) {
            throw new SecurityException("You are not authorized to delete this post.");
        }

        postRepository.delete(post);
    }

    // =========================================================
    // LIKES
    // =========================================================

    @Transactional
    public Map<String, Object> toggleLike(Integer postId, User user) {
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post not found with id: " + postId));

        Optional<PostLike> existingLike = likeRepository.findByPostPostIdAndUserUserId(postId, user.getUserId());
        boolean liked;

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            post.setLikesCount(Math.max(0, post.getLikesCount() - 1));
            liked = false;
        } else {
            likeRepository.save(new PostLike(post, user));
            post.setLikesCount(post.getLikesCount() + 1);
            liked = true;
        }

        postRepository.save(post);

        Map<String, Object> result = new HashMap<>();
        result.put("liked", liked);
        result.put("likesCount", post.getLikesCount());
        result.put("postId", postId);
        return result;
    }

    // =========================================================
    // COMMENTS
    // =========================================================

    public List<CommentResponseDto> getComments(Integer postId, Integer currentUserId) {
        return commentRepository.findByPostPostIdOrderByCreatedAtAsc(postId).stream()
                .map(c -> CommentResponseDto.fromEntity(c, currentUserId))
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentResponseDto addComment(Integer postId, String commentText, User user) {
        if (commentText == null || commentText.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment text cannot be empty.");
        }

        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post not found with id: " + postId));

        PostComment comment = new PostComment(post, user, commentText.trim());
        comment = commentRepository.save(comment);

        post.setCommentsCount(post.getCommentsCount() + 1);
        postRepository.save(post);

        return CommentResponseDto.fromEntity(comment, user.getUserId());
    }

    @Transactional
    public void deleteComment(Integer commentId, User user) {
        PostComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("Comment not found with id: " + commentId));

        if (!comment.getUser().getUserId().equals(user.getUserId())) {
            throw new SecurityException("You are not authorized to delete this comment.");
        }

        CommunityPost post = comment.getPost();
        commentRepository.delete(comment);

        if (post != null) {
            post.setCommentsCount(Math.max(0, post.getCommentsCount() - 1));
            postRepository.save(post);
        }
    }

    // =========================================================
    // DESTINATION REVIEWS
    // =========================================================

    public Map<String, Object> getDestinationReviews(Integer destinationId, Integer currentUserId) {
        List<DestinationReview> reviews = reviewRepository.findByDestinationIdOrderByCreatedAtDesc(destinationId);
        Double avgRating = reviewRepository.getAverageRatingForDestination(destinationId);

        List<DestinationReviewDto> reviewDtos = reviews.stream()
                .map(r -> DestinationReviewDto.fromEntity(r, currentUserId))
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("destinationId", destinationId);
        response.put("totalReviews", reviews.size());
        response.put("averageRating", avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 0.0);
        response.put("reviews", reviewDtos);
        return response;
    }

    @Transactional
    public DestinationReviewDto addDestinationReview(Integer destinationId, CreateReviewRequest req, User user) {
        if (req.getRating() == null || req.getRating() < 1 || req.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }
        if (req.getReviewText() == null || req.getReviewText().trim().isEmpty()) {
            throw new IllegalArgumentException("Review text cannot be empty.");
        }

        TouristPlace place = touristPlaceRepository.findById(destinationId)
                .orElseThrow(() -> new NoSuchElementException("Destination not found with id: " + destinationId));

        DestinationReview review = new DestinationReview(
                destinationId,
                user,
                req.getRating(),
                req.getReviewText().trim(),
                req.getVisitDate() != null ? req.getVisitDate().trim() : "Recent"
        );
        review = reviewRepository.save(review);

        // Update destination review count and rating
        Double newAvg = reviewRepository.getAverageRatingForDestination(destinationId);
        if (newAvg != null) {
            place.setRating(Math.round(newAvg * 10.0) / 10.0);
        }
        place.setReviewCount((place.getReviewCount() != null ? place.getReviewCount() : 0) + 1);
        touristPlaceRepository.save(place);

        return DestinationReviewDto.fromEntity(review, user.getUserId());
    }

    // =========================================================
    // REPORT POST
    // =========================================================

    @Transactional
    public void reportPost(Integer postId, String reason, User user) {
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post not found with id: " + postId));

        if (reason == null || reason.trim().isEmpty()) {
            reason = "Inappropriate content";
        }

        if (!reportRepository.existsByPostPostIdAndUserUserId(postId, user.getUserId())) {
            reportRepository.save(new PostReport(post, user, reason.trim()));
            post.setIsReported(true);
            postRepository.save(post);
        }
    }

    // =========================================================
    // USER PROFILE
    // =========================================================

    public UserProfileDto getUserProfile(Integer profileUserId, Integer currentUserId) {
        User user = userRepository.findById(profileUserId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + profileUserId));

        List<CommunityPost> userPosts = postRepository.findByUserUserIdOrderByCreatedAtDesc(profileUserId);
        List<DestinationReview> userReviews = reviewRepository.findByUserUserIdOrderByCreatedAtDesc(profileUserId);

        int totalLikes = userPosts.stream().mapToInt(p -> p.getLikesCount() != null ? p.getLikesCount() : 0).sum();

        List<PostResponseDto> postDtos = userPosts.stream().map(p -> {
            boolean isLiked = currentUserId != null && likeRepository.existsByPostPostIdAndUserUserId(p.getPostId(), currentUserId);
            return PostResponseDto.fromEntity(p, currentUserId, isLiked);
        }).collect(Collectors.toList());

        List<DestinationReviewDto> reviewDtos = userReviews.stream()
                .map(r -> DestinationReviewDto.fromEntity(r, currentUserId))
                .collect(Collectors.toList());

        return new UserProfileDto(
                UserDto.fromEntity(user),
                userPosts.size(),
                userReviews.size(),
                totalLikes,
                postDtos,
                reviewDtos
        );
    }

    @Transactional
    public UserDto updateUserProfile(User user, UserDto dto) {
        if (dto.getFullName() != null && !dto.getFullName().trim().isEmpty()) {
            user.setFullName(dto.getFullName().trim());
        }
        if (dto.getBio() != null) {
            user.setBio(dto.getBio().trim());
        }
        if (dto.getAvatarUrl() != null && !dto.getAvatarUrl().trim().isEmpty()) {
            user.setAvatarUrl(dto.getAvatarUrl().trim());
        }
        user = userRepository.save(user);
        return UserDto.fromEntity(user);
    }

    // =========================================================
    // DESTINATIONS LIST
    // =========================================================

    public List<Map<String, Object>> getAllDestinations() {
        return touristPlaceRepository.findAll().stream().map(p -> {
            Map<String, Object> map = new HashMap<>();
            map.put("placeId", p.getPlaceId());
            map.put("placeName", p.getPlaceName());
            map.put("location", p.getLocation());
            map.put("category", p.getCategory());
            map.put("rating", p.getRating());
            map.put("imageUrl", p.getImageUrl());
            return map;
        }).collect(Collectors.toList());
    }
}

