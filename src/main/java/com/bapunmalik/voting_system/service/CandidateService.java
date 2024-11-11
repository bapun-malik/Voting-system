package com.bapunmalik.voting_system.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bapunmalik.voting_system.models.Candidate;
import com.bapunmalik.voting_system.models.dto.CandidateWithVoteCount;
import com.bapunmalik.voting_system.repository.CandidateRepository;
import com.bapunmalik.voting_system.repository.VoteRepository;

@Service
public class CandidateService {
    @Autowired
    private CandidateRepository candidateRepo;

    @Autowired
    private VoteRepository voteRepository;

    public List<Candidate> getCandidatesByConstituency(String constituency) {
        return candidateRepo.findByConstituency(constituency);
    }

    public List<CandidateWithVoteCount> getCandidatesWithVoteCountByConstituency(String constituency) {
        List<Candidate> candidates = candidateRepo.findByConstituency(constituency);
        return candidates.stream().map(candidate -> {
            int voteCount = voteRepository.getVoteCountByCandidate(candidate.getId()).orElse(0);
            return new CandidateWithVoteCount(candidate, voteCount);
        }).collect(Collectors.toList());
    }



    public List<String> getAllConstituencies() {
        return candidateRepo.findAllConstituencies();
    }

    public List<Candidate> getAllCandidates(){
        return candidateRepo.findAll();
    }

    public Candidate findCandidateById(Long candidateId) {
        return candidateRepo.findById(candidateId).get();
    }

     public List<CandidateWithVoteCount> getCandidatesWithVoteCounts() {
        List<Candidate> candidates = candidateRepo.findAll();
        
        return candidates.stream().map(candidate -> {
            int voteCount = voteRepository.getVoteCountByCandidate(candidate.getId()).orElse(0);
            return new CandidateWithVoteCount(candidate, voteCount);
        }).collect(Collectors.toList());
    }

     // Declare winner for the constituency
     public Candidate declareWinner(String constituency) {
        Long winnerId=voteRepository.findTopCandidateByConstituency(constituency);
        System.out.println("Winner Id="+winnerId);
        Candidate winner = candidateRepo.findById(winnerId).orElse(null);
        if (winner != null && winner.getConstituency().equals(constituency)) {
            winner.setWinner(true); // Mark the winner
            candidateRepo.save(winner);
            return winner;
        }
        return null;
    }


}
