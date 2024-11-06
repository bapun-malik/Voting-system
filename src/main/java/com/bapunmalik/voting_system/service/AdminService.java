package com.bapunmalik.voting_system.service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bapunmalik.voting_system.models.Candidate;
import com.bapunmalik.voting_system.models.Complaint;

import com.bapunmalik.voting_system.repository.CandidateRepository;
import com.bapunmalik.voting_system.repository.ComplaintRepository;


import java.util.List;

@Service
public class AdminService {


    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private ComplaintRepository complaintRepository;

    public long getTotalCandidates() {
        return candidateRepository.count();
    }

 
    public long getPendingComplaintCount() {
        return complaintRepository.countByResolved(false);
    }

    public List<Candidate> getAllCandidates() {
        return candidateRepository.findAll();
    }

    public void addCandidate(Candidate candidate) {
        candidateRepository.save(candidate);
    }



    public List<Complaint> getPendingComplaints() {
        return complaintRepository.findByResolved(false);
    }
}
