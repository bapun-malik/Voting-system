package com.bapunmalik.voting_system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bapunmalik.voting_system.models.Candidate;
import com.bapunmalik.voting_system.models.User;
import com.bapunmalik.voting_system.models.Vote;
import com.bapunmalik.voting_system.repository.CandidateRepository;
import com.bapunmalik.voting_system.repository.UserRepository;
import com.bapunmalik.voting_system.repository.VoteRepository;




@Service
public class VoteService {

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private CandidateRepository candidateRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public boolean castVote(Long candidateId, String constituency,Long userId) {
        // Find the candidate by ID
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new IllegalArgumentException("Candidate not found"));

        // Retrieve or create a vote record for this candidate in the specified constituency
        Vote vote = voteRepository.findByCandidateAndConstituency(candidate, constituency)
                .orElseGet(() -> new Vote(null, candidate, constituency, 0));

        // Increment the vote count
        vote.setVotes(vote.getVotes() + 1);
        voteRepository.save(vote);
        User user=userRepository.findById(userId).get();
        System.out.println(user);
        user.setVoted(true);       
        return true; // Indicate the vote was cast successfully
    }

    public int getTotalVotesForConstituency(String constituency) {
        return voteRepository.findTotalVotesByConstituency(constituency).orElse(0); // Default to 0 if no votes found
    }
}