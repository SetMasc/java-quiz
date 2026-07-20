package com.las.test_quiz.service;

import com.las.test_quiz.model.User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class QuizUserManager {
    private final Map<String, User> activeUsers = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);


    public User createUser(String username){
        User u = new User();
        u.setUsername(username);
        u.setUserId(idCounter.getAndIncrement());
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

    private User getUser(String token){
        return activeUsers.get(token);
    }

    public Optional<User> findUser(String token){
        if(token == null){
            return Optional.empty();
        }
        return Optional.ofNullable(getUser(token));
    }


    public Map<String, User> getAllUsers(){
        return activeUsers;
    }
}
