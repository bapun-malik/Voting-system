package com.bapunmalik.voting_system.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bapunmalik.voting_system.models.Candidate;
import com.bapunmalik.voting_system.models.dto.CandidateWithVoteCount;
import com.bapunmalik.voting_system.service.CandidateService;
import com.bapunmalik.voting_system.service.VotingService;

@RestController
@RequestMapping("/admin/api")
public class ResultController {

    @Autowired
    private CandidateService candidateService;

    @Autowired
    private VotingService votingService;

    // Fetch candidates for a selected constituency
    @GetMapping("/results/{constituency}")
    public ResponseEntity<Map<String, Object>> getCandidatesByConstituency(@PathVariable String constituency) {
        List<CandidateWithVoteCount> candidates=candidateService.getCandidatesWithVoteCountByConstituency(constituency);
        boolean winnerDeclared=votingService.isWinnerDeclared();
        Map<String, Object> response = new HashMap<>();
        response.put("candidates", candidates);
        response.put("winnerDeclared",winnerDeclared);
        return ResponseEntity.ok(response);
    }

    // Declare winner for a selected constituency
    @PostMapping("/declare-winner")
    public ResponseEntity<Map<String, Object>> declareWinner(@RequestBody Map<String, String> payload) {
        boolean status=votingService.isVotingOpen();
        String constituency = payload.get("constituency");
        Map<String, Object> response = new HashMap<>();
        if(!status){
            Candidate winner = candidateService.declareWinner(constituency);
            if (winner != null) {
                response.put("success", true);
                response.put("winnerName", winner.getName());
                response.put("constituency", constituency);
            } else {
                response.put("message", "Error in Winner Declaration");
            }
        }else{
            response.put("success",false);
            response.put("message","Voting is in Process");
        }

        return ResponseEntity.ok(response);
    }
}
