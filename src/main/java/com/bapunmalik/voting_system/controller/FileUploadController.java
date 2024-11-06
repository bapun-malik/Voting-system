package com.bapunmalik.voting_system.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api")
public class FileUploadController {

    // Directory to save uploaded files
    private static final String UPLOAD_DIR = "uploads/";

    @PostMapping("/upload")
    public String handleFileUpload(
            @RequestParam("aadharNumber") String aadharNumber,
            @RequestParam("photo") MultipartFile photo,
            @RequestParam("signature") MultipartFile signature,
            RedirectAttributes redirectAttributes) {
        
        if (aadharNumber.isEmpty() || !aadharNumber.matches("\\d{12}")) {
            return "Invalid Aadhaar number.";
        }

        try {
            // Create the uploads directory if it doesn't exist
            File directory = new File(UPLOAD_DIR);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Process and save the photo
            saveFile(photo, aadharNumber + "_photo");

            // Process and save the signature
            saveFile(signature, aadharNumber + "_signature");

            return "Files uploaded successfully.";
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to upload files.";
        }
    }

    private void saveFile(MultipartFile file, String newFileName) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("File is empty.");
        }

        // Save the file to the server
        Path path = Paths.get(UPLOAD_DIR + newFileName + getFileExtension(file.getOriginalFilename()));
        Files.write(path, file.getBytes());
    }

    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return dotIndex == -1 ? "" : filename.substring(dotIndex); // Return the file extension
    }
}
