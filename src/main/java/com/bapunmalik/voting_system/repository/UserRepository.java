package com.bapunmalik.voting_system.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bapunmalik.voting_system.models.User;
import java.util.List;
import java.util.Optional;




@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    long countByState(String state);
    Optional<User> findById(Long id);
    User findByVoterId(String voter_id);
    Optional<User> findByEmail(String email);
    Page<User> findAll(Pageable pageable);
    Page<User> findByApproved(boolean approved, Pageable pageable);
    long countByDistrictAndApproved(String constituency,boolean approved);
    
}
