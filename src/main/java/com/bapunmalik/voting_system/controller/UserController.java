package com.bapunmalik.voting_system.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.bapunmalik.voting_system.configs.ByteArrayMultipartFile;
import com.bapunmalik.voting_system.models.TempUser;
import com.bapunmalik.voting_system.models.User;
import com.bapunmalik.voting_system.service.OtpService;
import com.bapunmalik.voting_system.service.UserService;
import com.bapunmalik.voting_system.service.VoterIdGeneratorService;

import jakarta.servlet.http.HttpSession;

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

    @Value("${project.poster}")
    private String path;

    @ModelAttribute("user")
    public User user() {
        return new User();
    }

    @GetMapping("/signup")
    public String showSignUpForm(Model model) {
        model.addAttribute("user", new User());
        return "signup";
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

    // @PostMapping("/verify-otp")
    // @ResponseBody
    // public ResponseEntity<Map<String, Object>> verifyOtp(@RequestBody Map<String,
    // String> requestBody) {
    // String otp = requestBody.get("otp");
    // String email = requestBody.get("email");

    // Map<String, Object> response = new HashMap<>();

    // System.out.println("\n\nemail is :"+email+"\n OTP :"+otp);
    // if (otpService.validateOtp(email,otp)) {

    // User user = userService.getTempUser();
    // byte[] photoBytes = userService.getTempPhotoBytes();
    // byte[] signatureBytes = userService.getTempSignatureBytes();

    // if (user != null && photoBytes != null && signatureBytes != null) {
    // try {
    // // Save files
    // String voterId = voterIdGeneratorService.generateVoterId(user.getState());
    // user.setVoterId(voterId);

    // // Create the uploads directory if it doesn't exist
    // Path uploadPath = Paths.get(path);
    // if (!Files.exists(uploadPath)) {
    // Files.createDirectories(uploadPath);
    // }

    // MultipartFile photoFile = convertToMultipartFile(photoBytes, "photo.jpg");
    // MultipartFile signatureFile = convertToMultipartFile(signatureBytes,
    // "signature.jpg");
    // String photoFileName = saveFile(photoFile, user.getAadhar() + "_photo");
    // String signatureFileName = saveFile(signatureFile, user.getAadhar() +
    // "_signature");
    // // Save photo and signature

    // user.setPhotoFileName(photoFileName);
    // user.setSignatureFileName(signatureFileName);
    // user.setApproved(false);

    // // Save user to the database
    // userService.saveUser(user);
    // userService.clearTempUser();
    // } catch (IOException e) {
    // e.printStackTrace();
    // response.put("success", false);
    // return ResponseEntity.ok(response);
    // }

    // response.put("success", true);
    // }
    // } else {
    // response.put("success", false);
    // response.put("message", "Invalid OTP.");
    // }

    // return ResponseEntity.ok(response);
    // }

    // @PostMapping("/register")
    // public String handleRegistration(@ModelAttribute User user,
    // @RequestParam("photo") MultipartFile photo,
    // @RequestParam("signature") MultipartFile signature,
    // @RequestParam(value = "otp", required = false) String Uotp,
    // HttpSession session,
    // Model model) {
    // if (otpService.validateOtp(Uotp,(String)session.getAttribute("otp"))) {
    // model.addAttribute("message", "OTP verified successfully!");
    // // Handle file upload and user registration logic here
    // if (user.getAadhar() == null ||
    // !user.getAadhar().toString().matches("\\d{12}")) {
    // model.addAttribute("error", "Invalid Aadhaar number.");
    // return "signup";
    // }

    // try {

    // String voterId = voterIdGeneratorService.generateVoterId(user.getState());
    // user.setVoterId(voterId);
    // // Create the uploads directory if it doesn't exist
    // Path uploadPath = Paths.get(path);
    // if (!Files.exists(uploadPath)) {
    // Files.createDirectories(uploadPath);
    // }

    // // Process and save the photo
    // String photoFileName = saveFile(photo, user.getAadhar() + "_photo");
    // String signatureFileName = saveFile(signature, user.getAadhar() +
    // "_signature");
    // user.setPhotoFileName(photoFileName);
    // user.setSignatureFileName(signatureFileName);
    // user.setApproved(false); // Default to false or as needed

    // // Update user entity with file names

    // // Save the user
    // userService.saveUser(user);

    // model.addAttribute("voterId", voterId);
    // } catch (IOException e) {
    // e.printStackTrace();
    // model.addAttribute("error", "Failed to upload files.");
    // return "signup";
    // }
    // }
    // return "success";
    // }

    @PostMapping("/register")
    public String handleRegistration(@ModelAttribute User user,
            @RequestParam("photo") MultipartFile photo,
            @RequestParam("signature") MultipartFile signature,
            Model model) {
        if (user.getAadhar() == null || !user.getAadhar().toString().matches("\\d{12}")) {
            model.addAttribute("error", "Invalid Aadhaar number.");
            return "signup";
        }

        try {
            String voterId = voterIdGeneratorService.generateVoterId(user.getState());
            user.setVoterId(voterId);

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
