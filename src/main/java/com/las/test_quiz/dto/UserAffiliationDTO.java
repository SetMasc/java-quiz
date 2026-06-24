package com.las.test_quiz.dto;

import lombok.Data;

@Data
public class UserAffiliationDTO {
    private boolean hasActiveSession;
    private String roomCode;
    private boolean isHost;

    public UserAffiliationDTO(boolean hasActiveSession, String roomCode, boolean isHost){
        this.hasActiveSession = hasActiveSession;
        this.roomCode = roomCode;
        this.isHost = isHost;
    }
}
