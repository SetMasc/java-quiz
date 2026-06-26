package com.las.test_quiz.service;

import com.las.test_quiz.dto.UserAffiliationDTO;
import com.las.test_quiz.dto.UserInRoomDTO;
import com.las.test_quiz.model.Room;
import com.las.test_quiz.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class QuizRoomManager {
    private final Map<String, Room> activeRooms = new ConcurrentHashMap<>();
    private final QuizUserManager userManager;

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

    public Map<String, String> addUserToRoom(String roomCode, String username, String token){
        Room room = activeRooms.get(roomCode);
        if(room == null){
            return Map.of("error", "Room not found");
        }

        synchronized (room){
            User user;
            if (token != null && userManager.getAllUsers().containsKey(token)) {
                user = userManager.getUser(token);
            } else {
                user = userManager.createUser(username);
            }

            if (room.getAdminHostToken() == null) {
                room.setAdminHostToken(user.getToken());
            }

            room.getUsers().put(user.getToken(), user);
            System.out.println("Player " + user.getUsername() + " joined room " + roomCode);

            Map<String, String> response = new HashMap<>();
            response.put("userId", user.getUserId().toString());
            response.put("token", user.getToken());

            return response;
        }
    }

    public List<UserInRoomDTO> getUsersInRoom(String roomCode){
        Room r = activeRooms.get(roomCode);
        List<UserInRoomDTO> result = new ArrayList<>();
        if(r != null){
            r.getUsers().forEach((s, u) ->{
                UserInRoomDTO userDTO = UserInRoomDTO.builder()
                        .userId(u.getUserId())
                        .score(u.getScore())
                        .username(u.getUsername())
                        .build();
                result.add(userDTO);
            }) ;
        }
        return result;
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

    public void closeRoom(String roomCode){
        activeRooms.remove(roomCode);
    }

    public Room getRoom(String roomCode){
        return activeRooms.get(roomCode);
    }

    public Map<String, Room> getAllRooms(){
        return activeRooms;
    }


    private String generate6DigitCode(){
        int num = ThreadLocalRandom.current().nextInt(100000, 999999);
        return String.valueOf(num);
    }
}
