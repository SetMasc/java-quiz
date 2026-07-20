package com.las.test_quiz.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String token) {
        super("User not found for token: " + token);
    }
}
