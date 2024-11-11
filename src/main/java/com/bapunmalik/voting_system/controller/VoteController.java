package com.bapunmalik.voting_system.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.bapunmalik.voting_system.models.Candidate;
import com.bapunmalik.voting_system.models.User;
import com.bapunmalik.voting_system.models.dto.CandidateWithVoteCount;
import com.bapunmalik.voting_system.repository.UserRepository;
import com.bapunmalik.voting_system.service.CandidateService;
import com.bapunmalik.voting_system.service.UserService;
import com.bapunmalik.voting_system.service.VoteService;
import com.bapunmalik.voting_system.service.VotingService;

@Controller
@RequestMapping("/vote")
public class VoteController {

    @Autowired
    private VoteService voteService;
    @Autowired
    private CandidateService candidateService;
    @Autowired
    private UserRepository userRepository;
     
    @Autowired
    private VotingService votingService;



    @GetMapping("/api/live-vote-counts")
    @ResponseBody
    public List<CandidateWithVoteCount> getLiveVoteCounts() {
            return candidateService.getCandidatesWithVoteCounts();
        }

    @GetMapping("")
    @PreAuthorize("hasRole('ROLE_USER')")
    public String showVotePage(Model model,Authentication authentication){
        String loggedInUser = authentication.getName();
        User log=userRepository.findByEmail(loggedInUser).get();
        boolean status=votingService.isVotingOpen();
        System.out.println(log);
        model.addAttribute("user",log);
        List<Candidate> candidates=candidateService.getAllCandidates();
        model.addAttribute("candidates",candidates);
        model.addAttribute("status",status);
        return "vote";
    }

    
    @PostMapping("/{candidateId}/{userId}")
    public ResponseEntity<String> castVote(@PathVariable Long candidateId,@PathVariable Long userId, @RequestParam("constituency") String constituency) {
        // Cast vote through the service
        try {
            voteService.castVote(candidateId, constituency,userId);
            return ResponseEntity.ok("Vote cast successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Candidate not found");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error casting vote");
        }
    }

    @GetMapping("/total/{constituency}")
    public ResponseEntity<Integer> getTotalVotesByConstituency(@PathVariable String constituency) {
        Integer totalVotes = voteService.getTotalVotesForConstituency(constituency);
        return ResponseEntity.ok(totalVotes);
    }

     

}
