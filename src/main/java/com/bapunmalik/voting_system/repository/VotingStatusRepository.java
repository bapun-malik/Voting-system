package com.bapunmalik.voting_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bapunmalik.voting_system.models.VotingStatus;

@Repository
public interface VotingStatusRepository extends JpaRepository<VotingStatus,Long> {

    VotingStatus findTopByOrderByUpdatedAtDesc();
   
}
