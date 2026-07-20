package com.las.test_quiz.dto;

import lombok.Builder;

@Builder
public record UserInRoomDTO(
    Long userId,
    String username,
    int score
){}
