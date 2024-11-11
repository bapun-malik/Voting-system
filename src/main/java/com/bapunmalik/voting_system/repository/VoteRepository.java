package com.bapunmalik.voting_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bapunmalik.voting_system.models.Candidate;
import com.bapunmalik.voting_system.models.Vote;

import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByCandidateAndConstituency(Candidate candidate, String constituency);
    @Query("SELECT v.votes FROM Vote v WHERE v.candidate.id = :candidateId")
    Integer findTotalVotesByCandidateId(@Param("candidateId") Long candidateId);
    @Query("SELECT v.votes FROM Vote v WHERE v.candidate.id = :candidateId")
    Optional<Integer> getVoteCountByCandidate(@Param("candidateId") Long candidateId);

    @Query("SELECT SUM(CASE WHEN v.constituency = :constituency THEN v.votes ELSE 0 END) AS totalVotesByConstituency, " +
       "SUM(CASE WHEN v.candidate.name = 'Nota' AND v.constituency = :constituency THEN v.votes ELSE 0 END) AS totalVotesForNota " +
       "FROM Vote v")
    Object[] findTotalVotesByConstituencyAndNota(@Param("constituency") String constituency);

    @Query("SELECT SUM(v.votes) FROM Vote v WHERE v.constituency = :constituency")
    Optional<Integer> findTotalVotesByConstituency(String constituency);

    @Query("SELECT v.candidate.id FROM Vote v " +
       "WHERE v.constituency = :constituency " +
       "GROUP BY v.candidate.id " +
       "HAVING SUM(v.votes) = (" +
           "SELECT MAX(total_votes) FROM (" +
               "SELECT SUM(votes) as total_votes " +
               "FROM Vote " +
               "WHERE constituency = :constituency " +
               "GROUP BY candidate.id" +
           ")" +
       ")")
    Long findTopCandidateByConstituency(String constituency);
    

}