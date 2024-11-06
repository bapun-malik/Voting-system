package com.bapunmalik.voting_system.models;

import org.springframework.web.multipart.MultipartFile;

public class TempUser {
    private User user;
    private MultipartFile photo;
    private MultipartFile signature;

    public TempUser(User user, MultipartFile photo, MultipartFile signature) {
        this.user = user;
        this.photo = photo;
        this.signature = signature;
    }

    public User getUser() {
        return user;
    }

    public MultipartFile getPhoto() {
        return photo;
    }

    public MultipartFile getSignature() {
        return signature;
    }
}
