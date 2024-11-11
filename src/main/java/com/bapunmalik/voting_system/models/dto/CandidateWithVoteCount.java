package com.bapunmalik.voting_system.models.dto;

import com.bapunmalik.voting_system.models.Candidate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class CandidateWithVoteCount {
    private Candidate candidate;
    private int voteCount;
    public CandidateWithVoteCount(Candidate candidate, int voteCount) {
        this.candidate = candidate;
        this.voteCount = voteCount;
    }

    public String getName() { return candidate.getName(); }
    public String getParty() { return candidate.getParty(); }
    public String getConstituency() { return candidate.getConstituency();}
    public String getSymbol(){return candidate.getSymbol();}
    public String getPhotoFilename(){return candidate.getPhotoFilename();}
    public Long getId(){return candidate.getId();}
    public boolean isWinner(){return candidate.isWinner();}
    public int getVoteCount() { return voteCount; }
}