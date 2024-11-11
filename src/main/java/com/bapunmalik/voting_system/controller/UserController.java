package com.bapunmalik.voting_system.controller;


import com.bapunmalik.voting_system.models.User;
import com.bapunmalik.voting_system.service.OtpService;
import com.bapunmalik.voting_system.service.UserService;
import com.bapunmalik.voting_system.service.VoterIdGeneratorService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Controller
@RequestMapping("/users")
@SessionAttributes("user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private VoterIdGeneratorService voterIdGeneratorService;


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Cloudinary cloudinary; // Inject Cloudinary to upload files to Cloudinary


    @ModelAttribute("user")
    public User user() {
        return new User();
    }

    @GetMapping("/signup")
    public String showSignUpForm(Model model) {
        model.addAttribute("user", new User());
        return "voter-registration";
    }

    @PostMapping("/register")
    public String handleRegistration(@ModelAttribute User user,
                                     @RequestParam("photo") MultipartFile photo,
                                     @RequestParam("signature") MultipartFile signature,
                                     Model model) {
        if (user.getAadhar() == null || !user.getAadhar().toString().matches("\\d{12}")) {
            model.addAttribute("error", "Invalid Aadhaar number.");
            return "voter-registration";
        }

        try {
            String voterId = voterIdGeneratorService.generateVoterId(user.getState());
            user.setVoterId(voterId);
            user.setRole("USER");
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // Upload photo and signature to Cloudinary
            String photoFileName = uploadToCloudinary(photo, user.getAadhar() + "_photo");
            String signatureFileName = uploadToCloudinary(signature, user.getAadhar() + "_signature");

            user.setPhotoFileName(photoFileName);
            user.setSignatureFileName(signatureFileName);
            user.setApproved(false); // Default to false or as needed

            // Save user details
            userService.saveUser(user);

            model.addAttribute("voterId", voterId);
            return "success";
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error", "Failed to upload files.");
            return "voter-registration";
        }
    }

    // Method to upload files to Cloudinary
    private String uploadToCloudinary(MultipartFile file, String newFileName) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("File is empty.");
        }
    
        // Validate file type and size
        if (!isValidFile(file)) {
            throw new IllegalArgumentException("Invalid file type or size.");
        }
    
        // Stream the file directly to Cloudinary
        try (InputStream fileInputStream = file.getInputStream()) {
            @SuppressWarnings("unchecked")
            Map<String, String> uploadResult = cloudinary.uploader().upload(fileInputStream,
                    ObjectUtils.asMap(
                            "public_id", newFileName,  // Set custom public ID
                            "resource_type", "image",  // Explicitly set as image
                            "quality", "auto",         // Auto optimize quality
                            "fetch_format", "auto"));  // Auto convert to the best format
    
            return uploadResult.get("url"); // Return the Cloudinary URL
        } catch (IOException e) {
            // Log the exception or rethrow it based on your logging strategy
            throw new IOException("File upload failed: " + e.getMessage(), e);
        }
    }
    
    private boolean isValidFile(MultipartFile file) {
        // File size validation (example: max 10MB)
        long maxSize = 10 * 1024 * 1024; // 10MB
        if (file.getSize() > maxSize) {
            return false;
        }
    
        // Mime-type validation (example: only allow images)
        String mimeType = file.getContentType();
        return mimeType != null && mimeType.startsWith("image");
    }
    
}
