package com.las.test_quiz.exception;

public class RoomNotFoundException extends RuntimeException {
    public RoomNotFoundException(String roomCode) {
        super("Room not found: " + roomCode);
    }
}
