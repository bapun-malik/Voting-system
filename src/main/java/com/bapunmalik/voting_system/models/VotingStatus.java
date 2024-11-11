package com.bapunmalik.voting_system.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VotingStatus {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "is_voting_open")
    private boolean isVotingOpen;

    @Column(name="is_winner_declared")
    private boolean isWinnerDeclared;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
