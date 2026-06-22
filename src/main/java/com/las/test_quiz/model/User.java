package com.las.test_quiz.model;

import lombok.Data;

@Data
public class User {
    private String sessionId;
    private String username;
    private String token;
    private int score;
}
