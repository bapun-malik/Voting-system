package com.bapunmalik.voting_system.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.bapunmalik.voting_system.configs.ByteArrayMultipartFile;
import com.bapunmalik.voting_system.models.User;
import com.bapunmalik.voting_system.service.OtpService;
import com.bapunmalik.voting_system.service.UserService;
import com.bapunmalik.voting_system.service.VoterIdGeneratorService;

@Controller
@RequestMapping("/users")
@SessionAttributes("user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private VoterIdGeneratorService voterIdGeneratorService;

    @Autowired
    private OtpService otpService;


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${project.poster}")
    private String path;

    @ModelAttribute("user")
    public User user() {
        return new User();
    }

    @GetMapping("/signup")
    public String showSignUpForm(Model model) {
        model.addAttribute("user", new User());
        return "voter-registration";
    }

    @PostMapping("/generate-otp")
    @ResponseBody
    public String generateOtp(@ModelAttribute User user, @RequestParam("photo") MultipartFile photo,
            @RequestParam("signature") MultipartFile signature) {

        // Generate and send OTP
        otpService.sendOtp(user.getEmail());

        // Temporarily store user data (or in session)
        userService.saveTempUser(user, photo, signature);

        return "OTP sent to your email.";
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
            Path uploadPath = Paths.get(path);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Process and save the photo
            String photoFileName = saveFile(photo, user.getAadhar() + "_photo");
            String signatureFileName = saveFile(signature, user.getAadhar() + "_signature");
            user.setPhotoFileName(photoFileName);
            user.setSignatureFileName(signatureFileName);
            user.setApproved(false); // Default to false or as needed

            // Save the user
            userService.saveUser(user);

            model.addAttribute("voterId", voterId);
            return "success";
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error", "Failed to upload files.");
            return "signup";
        }
    }

    public String determineContentType(String filename) {
        String lowerCaseFilename = filename.toLowerCase();
        if (lowerCaseFilename.endsWith(".jpg") || lowerCaseFilename.endsWith(".jpeg")) {
            return MediaType.IMAGE_JPEG_VALUE;
        } else if (lowerCaseFilename.endsWith(".png")) {
            return MediaType.IMAGE_PNG_VALUE;
        } else {
            return MediaType.APPLICATION_OCTET_STREAM_VALUE; // Fallback for unknown types
        }
    }

    public MultipartFile convertToMultipartFile(byte[] fileBytes, String originalFilename) {
        String contentType = determineContentType(originalFilename);
        return new ByteArrayMultipartFile(fileBytes, "file", originalFilename, contentType);
    }

    private String saveFile(MultipartFile file, String newFileName) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("File is empty.");
        }

        // Save the file to the server
        String extension = getFileExtension(file.getOriginalFilename());
        Path filePath = Paths.get(path + newFileName + extension);
        Files.write(filePath, file.getBytes());
        return newFileName + extension;
    }

    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return dotIndex == -1 ? "" : filename.substring(dotIndex); // Return the file extension
    }
}
