package com.bapunmalik.voting_system.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.bapunmalik.voting_system.models.Candidate;
import com.bapunmalik.voting_system.models.User;
import com.bapunmalik.voting_system.models.dto.CandidateWithVoteCount;
import com.bapunmalik.voting_system.repository.CandidateRepository;
import com.bapunmalik.voting_system.service.CandidateService;
import com.bapunmalik.voting_system.service.UserService;
import com.bapunmalik.voting_system.service.VotingService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;


@Controller
@RequestMapping("/admin")
public class AdminController {


    @Autowired
    private CandidateRepository candidateRepo;

     @Autowired
    private CandidateService candidateService;


    @Autowired
    private UserService userService;

    
    @Autowired
    private VotingService votingService;

    @Autowired
    private Cloudinary cloudinary;

    @GetMapping("/dashboard")
    public String showAdmindashboard(){
        return "dashboard";
    }


    @GetMapping("/candidate-registration")
    public String CandidateRegistrationPage(Model model) {
        Candidate candidate=new Candidate();
        model.addAttribute(candidate);
        return "candidates";
    }
  
    @PostMapping("/candidate-registration")
    public String storeCandidate(@ModelAttribute Candidate candidate,
            @RequestParam("photo") MultipartFile photo, Model model) {
        try {
            // Upload photo to Cloudinary
            String photoFileName = uploadToCloudinary(photo, candidate.getParty() + candidate.getConstituency() + "_photo");

            // Set the Cloudinary URL or public ID as the filename
            candidate.setPhotoFilename(photoFileName);

            // Save candidate data
            candidateRepo.save(candidate);

            return "redirect:/admin/dashboard"; // Redirect to the admin dashboard

        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error", "Failed to upload files.");
            return "/dashboard"; // Return to the dashboard with an error message
        }
    }

    @GetMapping("/voter-verification")
    public String showVerificationPage(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<User> userPage = userService.getAllUsersPaginatedByApprovalStatus(false, page, size);
        model.addAttribute("userPage", userPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        
        return "voter-verification";
    }

    
    @PostMapping("/approve-voter")
    @ResponseBody
    public String approveVoter(@RequestParam Long userId) {
        User user = userService.getUserById(userId);
        if (user != null) {
            user.setApproved(true); // Set the approval status to true
            userService.saveUser(user); // Save the updated user
            return "User approved successfully!";
        }
        return "User not found!";
    }

    @PostMapping("/reject-voter")
    @ResponseBody
    public String rejectVoter(@RequestParam Long userId) {
        User user = userService.getUserById(userId);
        if (user != null) {
            userService.deleteUserById(userId);
            return "User deleted successfully!";
        }
        return "User not found!";
    }




    @GetMapping("/user-details/{userId}")
    @ResponseBody
    public User getUserDetails(@PathVariable Long userId) {
    return userService.getUserById(userId);
    }





    @PostMapping("/open-voting")
    public String openVoting(Model model) {
        votingService.openVoting();
        boolean isVotingOpen = votingService.isVotingOpen();
        model.addAttribute("isVotingOpen", isVotingOpen);
        model.addAttribute("status",isVotingOpen);
        model.addAttribute("message", "Voting has been opened.");
        return "redirect:/admin/live-monitoring";
    }

    @PostMapping("/close-voting")
    public String closeVoting(Model model) {
        votingService.closeVoting();
        boolean isVotingOpen = votingService.isVotingOpen();
        model.addAttribute("isVotingOpen", isVotingOpen);
        model.addAttribute("status",isVotingOpen);
        model.addAttribute("message", "Voting has been closed.");
        return "redirect:/admin/live-monitoring";
    }


    @GetMapping("/live-monitoring")
    public String showLiveMonitoringPage(Model model) {
    // Assume you have a service that checks if voting is open
        List<CandidateWithVoteCount> candidates=candidateService.getCandidatesWithVoteCounts();
        List<String> constituencies = candidateService.getAllConstituencies();
        boolean isVotingOpen = votingService.isVotingOpen();
        model.addAttribute("candidates",candidates);
        model.addAttribute("constituencies",constituencies);
        System.out.println("Voting Ststua="+isVotingOpen);
        model.addAttribute("isVotingOpen", isVotingOpen);
        model.addAttribute("status",isVotingOpen);
        return "live-monitoring"; // Name of your Thymeleaf template
    }


    @GetMapping("/voting-status")
    @ResponseBody
    public Map<String, Boolean> getVotingStatus() {
        Map<String, Boolean> response = new HashMap<>();
        response.put("isVotingOpen", votingService.isVotingOpen());
        return response;
    }


    @GetMapping("/publish-result")
    public String showResultPage(Model model){
        // Fetch all constituencies for the dropdown list
        List<String> constituencies = candidateService.getAllConstituencies();
        // Add constituencies to the model
        model.addAttribute("constituencies", constituencies);
        model.addAttribute("selectedConstituency", "");
        return "result";
    }



        // Method to upload file to Cloudinary
    private String uploadToCloudinary(MultipartFile file, String publicId) throws IOException {
        // Upload the file to Cloudinary
        File tempFile = convertMultipartFileToFile(file); // Convert MultipartFile to File
        @SuppressWarnings("unchecked")
        Map<String, String> uploadResult = cloudinary.uploader().upload(tempFile, 
                                            ObjectUtils.asMap("public_id", publicId));

        // Return the Cloudinary URL or public ID
        return uploadResult.get("secure_url");  // Or use public_id based on your requirement
    }

    // Method to convert MultipartFile to File
    private File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        File file = new File(multipartFile.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(multipartFile.getBytes());
        }
        return file;
    }

}
