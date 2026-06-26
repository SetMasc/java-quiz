package com.las.test_quiz.controller.rest;

import com.las.test_quiz.dto.RoomDTO;
import com.las.test_quiz.dto.UserAffiliationDTO;
import com.las.test_quiz.dto.UserInRoomDTO;
import com.las.test_quiz.model.Room;
import com.las.test_quiz.model.User;
import com.las.test_quiz.service.QuizRoomManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rooms")
public class RoomRestController {
    private final QuizRoomManager roomManager;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/create")
    public ResponseEntity<String> createRoom(){
        Room r = roomManager.createRoom();
        return ResponseEntity.ok(r.getRoomCode());
    }

    @GetMapping("/{roomCode}/get")
    public ResponseEntity<Object> getRoom(@PathVariable String roomCode){
        Room r = roomManager.getRoom(roomCode);
        if(r != null) {
            RoomDTO roomDTO = RoomDTO.builder()
                    .roomCode(r.getRoomCode())
                    .stats(r.getStats())
                    .currentQuestionId(r.getCurrentQuestionIndex())
                    .users(roomManager.getUsersInRoom(r.getRoomCode()))
                    .status(r.getStatus())
                    .build();
            return ResponseEntity.ok(roomDTO);
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
