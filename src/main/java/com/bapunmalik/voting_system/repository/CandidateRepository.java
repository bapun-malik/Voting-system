package com.bapunmalik.voting_system.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bapunmalik.voting_system.models.Candidate;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    @Query("SELECT DISTINCT c.constituency FROM Candidate c")
    List<String> findAllConstituencies();

    List<Candidate> findByConstituency(String constituency);
}
