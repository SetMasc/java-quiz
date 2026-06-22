package com.las.test_quiz.model;

import lombok.Data;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class Room {
    private String roomCode;
    private String adminSessionId;
    private String adminHostToken;

    private RoomStatus status = RoomStatus.LOBBY;
    private int currentQuestionIndex = 0;

    private final Map<String, User> users = new ConcurrentHashMap<>(); //todo
    private final Map<String, Long> stats = new ConcurrentHashMap<>();   //todo
}
