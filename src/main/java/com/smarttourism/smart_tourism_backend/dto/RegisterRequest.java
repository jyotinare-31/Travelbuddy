package com.smarttourism.smart_tourism_backend.dto;

public class RegisterRequest {
    private String fullName;
    private String email;
    private String password;
    private String bio;
    private String avatarUrl;

    public RegisterRequest() {
    }

    public RegisterRequest(String fullName, String email, String password, String bio, String avatarUrl) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.bio = bio;
        this.avatarUrl = avatarUrl;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}

