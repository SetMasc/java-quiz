package com.las.test_quiz.controller.rest;

import com.las.test_quiz.model.Room;
import com.las.test_quiz.model.User;
import com.las.test_quiz.service.QuizRoomManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rooms")
public class RoomRestController {
    private final QuizRoomManager roomManager;

    public RoomRestController(QuizRoomManager roomManager) {
        this.roomManager = roomManager;
    }

    @GetMapping("/create")
    public ResponseEntity<String> createRoom(){
        Room r = roomManager.createRoom();
        return ResponseEntity.ok(r.getRoomCode());
    }

    @GetMapping("/{roomCode}/join")
    public ResponseEntity<String> joinRoom(@PathVariable String roomCode){
        Room r = roomManager.getRoom(roomCode);
        if(r != null) {
            return ResponseEntity.ok("Join successfully!");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Room not found");
    }

    @GetMapping("/{roomCode}/lobby")
    public ResponseEntity<List<User>> lobbyRoom(@PathVariable String roomCode){
        Room r = roomManager.getRoom(roomCode);
        List<User> result = new ArrayList<>();
        Map<String, User> users = r.getUsers();
        users.forEach((s, user) -> {
            result.add(user);
        });
        return ResponseEntity.ok(result);
    }
}
