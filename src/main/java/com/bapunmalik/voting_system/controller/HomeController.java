package com.bapunmalik.voting_system.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.bapunmalik.voting_system.models.Notification;
import com.bapunmalik.voting_system.models.User;
import com.bapunmalik.voting_system.models.dto.CandidateWithVoteCount;
import com.bapunmalik.voting_system.repository.UserRepository;
import com.bapunmalik.voting_system.service.CandidateService;
import com.bapunmalik.voting_system.service.NotificationService;
import com.bapunmalik.voting_system.service.UserService;
import com.bapunmalik.voting_system.service.VotingService;

@Controller
public class HomeController {


    @Autowired
    private NotificationService notificationService;

    @Autowired
    private CandidateService candidateService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private VotingService votingService;

    @RequestMapping("/")
    public String showHomePage(Authentication authentication){
        if (authentication != null) {
            authentication.getAuthorities().forEach(auth -> 
                System.out.println("Current authority: " + auth.getAuthority())
            );
        }else{
            System.out.println("No Login Yet");
        }
        return "index";
    }
    @GetMapping("/feature")
    public String showFeaturePage(){
        return "feature";
    }

    @GetMapping("/about")
    public String showAboutPage(){
        return "about";
    }
    @GetMapping("/contact")
    public String showContactPage(){
        return "contact";
    }
    @GetMapping("/dashboard")
    @PreAuthorize("isAuthenticated()")
    public String showDashboardPage(@AuthenticationPrincipal UserDetails userDetails,
                                     Model model,
                                    Authentication authentication){
        List<Notification> notification=notificationService.getAllNotifications();
        List<CandidateWithVoteCount> candidates=candidateService.getCandidatesWithVoteCounts();
        boolean status=votingService.isVotingOpen();
        model.addAttribute("notifications",notification);
        model.addAttribute("candidates",candidates);
        model.addAttribute("status",status);
        
        // Get user by email (assuming email is the username in UserDetails)
        User user = userRepository.findByEmail(userDetails.getUsername()).get();
        Long totalVoter=userService.getUserCountByConstituency(user.getDistrict());                               
        
        if (user != null) {
            model.addAttribute("user", user); // Add user info to the model
            model.addAttribute("canVote", !user.isVoted()); // Check if user is eligible to vote
        }
        model.addAttribute("totalVoter",totalVoter);
         
        return "voter-dashboard";
    }
}
