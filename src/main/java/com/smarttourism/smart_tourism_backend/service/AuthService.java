package com.smarttourism.smart_tourism_backend.service;

import com.smarttourism.smart_tourism_backend.dto.AuthRequest;
import com.smarttourism.smart_tourism_backend.dto.AuthResponse;
import com.smarttourism.smart_tourism_backend.dto.RegisterRequest;
import com.smarttourism.smart_tourism_backend.dto.UserDto;
import com.smarttourism.smart_tourism_backend.entity.User;
import com.smarttourism.smart_tourism_backend.repository.UserRepository;
import com.smarttourism.smart_tourism_backend.security.JwtUtil;
import com.smarttourism.smart_tourism_backend.security.PasswordUtil;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class AuthService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse register(RegisterRequest req) {
        if (req == null) {
            throw new IllegalArgumentException("Registration details cannot be empty.");
        }
        if (req.getFullName() == null || req.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Full name is required.");
        }
        if (req.getFullName().trim().length() < 2) {
            throw new IllegalArgumentException("Full name must be at least 2 characters.");
        }
        if (req.getEmail() == null || req.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email address is required.");
        }

        String email = req.getEmail().trim().toLowerCase();
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Please enter a valid email address (e.g., name@example.com).");
        }

        if (req.getPassword() == null || req.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password is required.");
        }
        if (req.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long.");
        }

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("An account with this email address already exists. Please login instead.");
        }

        String hash = PasswordUtil.hashPassword(req.getPassword());
        String avatar = req.getAvatarUrl();
        if (avatar == null || avatar.trim().isEmpty()) {
            avatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=256&q=80";
        }

        User user = new User(
                req.getFullName().trim(),
                email,
                hash,
                avatar,
                req.getBio() != null && !req.getBio().trim().isEmpty() ? req.getBio().trim() : "Traveler exploring Solapur & beyond."
        );

        user = userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUserId(), user.getEmail(), user.getFullName());
        return new AuthResponse(token, UserDto.fromEntity(user), "User registered successfully");
    }

    public AuthResponse login(AuthRequest req) {
        if (req == null || req.getEmail() == null || req.getPassword() == null) {
            throw new IllegalArgumentException("Email and password are required.");
        }

        String email = req.getEmail().trim().toLowerCase();
        if (email.isEmpty() || req.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Email and password cannot be empty.");
        }

        Optional<User> optionalUser = userRepository.findByEmailIgnoreCase(email);

        if (optionalUser.isEmpty() || !PasswordUtil.verifyPassword(req.getPassword(), optionalUser.get().getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        User user = optionalUser.get();
        String token = jwtUtil.generateToken(user.getUserId(), user.getEmail(), user.getFullName());
        return new AuthResponse(token, UserDto.fromEntity(user), "Login successful");
    }

    public User getUserFromAuthHeader(String authHeader) {
        if (authHeader == null || authHeader.trim().isEmpty()) {
            return null;
        }
        Integer userId = jwtUtil.extractUserId(authHeader);
        if (userId == null) {
            return null;
        }
        return userRepository.findById(userId).orElse(null);
    }

    public User requireUser(String authHeader) {
        User user = getUserFromAuthHeader(authHeader);
        if (user == null) {
            throw new SecurityException("Unauthorized: Valid JWT Bearer token required.");
        }
        return user;
    }

    public UserDto getCurrentUserDto(String authHeader) {
        User user = requireUser(authHeader);
        return UserDto.fromEntity(user);
    }
}
