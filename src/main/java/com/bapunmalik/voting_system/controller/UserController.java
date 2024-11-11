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

        try {
            // Upload to Cloudinary
            @SuppressWarnings("unchecked")
            Map<String, String> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("public_id", newFileName, "resource_type", "auto"));
            
            // Get the URL of the uploaded file
            return uploadResult.get("url");  // This is the Cloudinary URL
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to Cloudinary", e);
        }
    }
}
