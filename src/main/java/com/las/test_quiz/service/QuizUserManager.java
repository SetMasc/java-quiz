package com.las.test_quiz.service;

import com.las.test_quiz.model.User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class QuizUserManager {
    private final Map<String, User> activeUsers = new ConcurrentHashMap<>();

    public User createUser(String sessionId, String username){
        User u = new User();
        u.setUsername(username);
        u.setSessionId(sessionId);
        u.setScore(0);
        String generatedToken;
        do{
            generatedToken = UUID.randomUUID().toString();
            u.setToken(generatedToken);
        }while (activeUsers.putIfAbsent(generatedToken, u) != null);
        return u;
    }

    public User incrementScore(String token, int value){
        User u = activeUsers.get(token);
        int score = u.getScore() + value;
        u.setScore(score);
        return u;
    }

    public void deleteUser(String token){
        activeUsers.remove(token);
    }

    public User getUser(String token){
        return activeUsers.get(token);
    }

    public Map<String, User> getAllUsers(){
        return activeUsers;
    }
}
