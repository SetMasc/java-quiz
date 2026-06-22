package com.las.test_quiz.service;

import com.las.test_quiz.model.Room;
import com.las.test_quiz.model.User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class QuizRoomManager {
    private final Map<String, Room> activeRooms = new ConcurrentHashMap<>();

    public Room createRoom(){
        String generatedCode;
        Room r = new Room();
        do{
            generatedCode = generate6DigitCode();
            r.setRoomCode(generatedCode);
        }while (activeRooms.putIfAbsent(generatedCode, r) != null);
        System.out.println("Room " + generatedCode + " created");
        return r;
    }

    private String generate6DigitCode(){
        int num = ThreadLocalRandom.current().nextInt(100000, 999999);
        return String.valueOf(num);
    }

    public Room getRoom(String roomCode){
        return activeRooms.get(roomCode);
    }

    public void closeRoom(String roomCode){
        activeRooms.remove(roomCode);
    }
}
