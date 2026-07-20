package com.las.test_quiz.service;

import com.las.test_quiz.dto.UserInRoomDTO;
import com.las.test_quiz.model.Room;
import com.las.test_quiz.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
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
            Optional<User> u = userManager.findUser(userToken);

            user = u.orElseGet(() -> userManager.createUser(username));

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
        Room room = getRoom(roomCode);
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

            Optional<User> u = userManager.findUser(userToken);

            u.ifPresent(user -> log.info("User {} exit form room {}", user.getUsername(), roomCode));

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
        Room r = getRoom(roomCode);
        r.getUsers().forEach((token, user)->{
            userManager.deleteUser(token);
        });
        activeRooms.remove(roomCode);
    }

    private Room getRoom(String roomCode){
        return activeRooms.get(roomCode);
    }

    public Optional<Room> findRoom(String roomCode){
        return Optional.ofNullable(getRoom(roomCode));
    }



    public Map<String, Room> getAllRooms(){
        return activeRooms;
    }


    private String generate6DigitCode(){
        int num = ThreadLocalRandom.current().nextInt(100000, 999999);
        return String.valueOf(num);
    }
}
