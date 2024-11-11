package com.bapunmalik.voting_system.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class FileUploadController {

    @Autowired
    private Cloudinary cloudinary;

    @PostMapping("/upload")
    public String handleFileUpload(
            @RequestParam("aadharNumber") String aadharNumber,
            @RequestParam("photo") MultipartFile photo,
            @RequestParam("signature") MultipartFile signature) {
        
        if (aadharNumber.isEmpty() || !aadharNumber.matches("\\d{12}")) {
            return "Invalid Aadhaar number.";
        }

        try {
            // Upload photo to Cloudinary
            Map<String, Object> photoUploadResult = uploadFileToCloudinary(photo, aadharNumber + "_photo");
            
            // Upload signature to Cloudinary
            Map<String, Object> signatureUploadResult = uploadFileToCloudinary(signature, aadharNumber + "_signature");

            return "Files uploaded successfully to Cloudinary.";
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to upload files.";
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> uploadFileToCloudinary(MultipartFile file, String newFileName) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("File is empty.");
        }

        // Upload file to Cloudinary
        return cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "public_id", newFileName
        ));
    }
}
