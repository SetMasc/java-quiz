package com.las.test_quiz.service;

import com.las.test_quiz.dto.UserAffiliationDTO;
import com.las.test_quiz.model.Room;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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

    public UserAffiliationDTO checkUserAffiliation(String user_token){
        List<Room> rooms = new ArrayList<>();

        String roomCode = null;
        boolean hasActiveSession = false;
        boolean isHost = false;

        activeRooms.forEach((s, room) -> rooms.add(room));
        for(Room room : rooms){
            if(room.getUsers().containsKey(user_token)){
                hasActiveSession = true;
                roomCode = room.getRoomCode();
                if(room.getAdminHostToken().equals(user_token)){
                    isHost = true;
                }
            }
        }
        return new UserAffiliationDTO(hasActiveSession, roomCode, isHost);
    }

    public Map<String, Room> getAllRooms(){
        return activeRooms;
    }
}
