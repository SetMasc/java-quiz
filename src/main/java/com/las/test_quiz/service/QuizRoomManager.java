package com.las.test_quiz.service;

import com.las.test_quiz.dto.RoomDTO;
import com.las.test_quiz.dto.UserAffiliationDTO;
import com.las.test_quiz.dto.UserInRoomDTO;
import com.las.test_quiz.model.Room;
import com.las.test_quiz.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
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
        log.info("Room {} created", generatedCode);
        return r;
    }

    public Map<String, String> addUserToRoom(String roomCode, String username, String userToken){
        Room room = activeRooms.get(roomCode);
        if(room == null){
            return Map.of("error", "Room not found");
        }

        synchronized (room){
            User user;
            if (userToken != null && userManager.getAllUsers().containsKey(userToken)) {
                user = userManager.getUser(userToken);
            } else {
                user = userManager.createUser(username);
            }

            if (room.getAdminHostToken() == null) {
                room.setAdminHostToken(user.getToken());
            }

            room.getUsers().put(user.getToken(), user);
            log.info("User {} joined to room {}", user.getUsername(), roomCode);

            Map<String, String> response = new HashMap<>();
            response.put("userId", user.getUserId().toString());
            response.put("token", user.getToken());

            return response;
        }
    }

    public Map<String, String> deleteUserFromRoom(String roomCode, String userToken){
        Room room = activeRooms.get(roomCode);
        if(room == null){
            return Map.of("error", "Room not found");
        }
        if(userToken == null){
            return Map.of("error", "User not found");
        }


        synchronized (room){
            Map<String, User> users = room.getUsers();

            if(room.getAdminHostToken().equals(userToken)){
                closeRoom(roomCode);
                log.info("Room {} deleted", roomCode);
            }else{
                users.remove(userToken);
            }

            log.info("User {} exit form room {}", userManager.getUser(userToken).getUsername(), roomCode);
            userManager.deleteUser(userToken);
            return Map.of("result", "success");

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
