package com.las.test_quiz.dto;

import com.las.test_quiz.model.RoomStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class RoomDTO {
    private String roomCode;
    private RoomStatus status;
    private int currentQuestionId;

    private List<UserInRoomDTO> users;
    private Map<String, Long> stats;
}
