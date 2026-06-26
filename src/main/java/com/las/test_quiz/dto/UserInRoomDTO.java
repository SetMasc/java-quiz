package com.las.test_quiz.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInRoomDTO {
    private Long userId;
    private String username;
    private int score;
}
