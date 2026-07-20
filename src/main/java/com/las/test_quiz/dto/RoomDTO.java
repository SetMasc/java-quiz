package com.las.test_quiz.dto;

import com.las.test_quiz.model.RoomStatus;
import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record RoomDTO(
    String roomCode,
    RoomStatus status,
    int currentQuestionId,

    List<UserInRoomDTO> users,
    Map<String, Long> stats
){}
