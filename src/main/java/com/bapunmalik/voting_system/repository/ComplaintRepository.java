package com.bapunmalik.voting_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bapunmalik.voting_system.models.Complaint;

import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    long countByResolved(boolean resolved);
    List<Complaint> findByResolved(boolean resolved);
}
