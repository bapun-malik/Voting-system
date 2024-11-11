package com.bapunmalik.voting_system.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bapunmalik.voting_system.models.VotingStatus;
import com.bapunmalik.voting_system.repository.VotingStatusRepository;

@Service
public class VotingService {

    @Autowired
    private VotingStatusRepository votingStatusRepository;

    public void openVoting() {
        VotingStatus votingStatus = new VotingStatus();
        votingStatus.setVotingOpen(true);
        votingStatus.setUpdatedAt(LocalDateTime.now());
        votingStatusRepository.save(votingStatus);
    }

    public void closeVoting() {
        VotingStatus votingStatus = new VotingStatus();
        votingStatus.setVotingOpen(false);
        votingStatus.setUpdatedAt(LocalDateTime.now());
        votingStatusRepository.save(votingStatus);
    }

    public boolean isVotingOpen() {
        VotingStatus status = votingStatusRepository.findTopByOrderByUpdatedAtDesc();
        System.out.println("The databse Status="+status);
        return status != null && status.isVotingOpen();
    }
    public boolean isWinnerDeclared() {
        VotingStatus status = votingStatusRepository.findTopByOrderByUpdatedAtDesc();
        System.out.println("The databse Status="+status);
        return status != null && status.isWinnerDeclared();
    }
}
