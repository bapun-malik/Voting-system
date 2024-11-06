package com.bapunmalik.voting_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bapunmalik.voting_system.models.Candidate;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {
}
