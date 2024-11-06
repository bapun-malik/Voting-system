package com.bapunmalik.voting_system.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bapunmalik.voting_system.models.User;
import java.util.List;




@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    long countByState(String state);
    User findByVoterId(String voter_id);
    User findByEmail(String email);
}
