package com.las.test_quiz.controller.rest;

import com.las.test_quiz.dto.RoomDTO;
import com.las.test_quiz.exception.RoomNotFoundException;
import com.las.test_quiz.model.Room;
import com.las.test_quiz.service.QuizRoomManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

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
        Room r = roomManager.findRoom(roomCode)
                .orElseThrow(() -> new RoomNotFoundException(roomCode));
        return ResponseEntity.ok(RoomDTO.builder()
                .roomCode(r.getRoomCode())
                .stats(r.getStats())
                .currentQuestionId(r.getCurrentQuestionIndex())
                .users(roomManager.getUsersInRoom(r.getRoomCode()))
                .status(r.getStatus())
                .build());
    }

    @PostMapping("/{roomCode}/check-admin")
    public ResponseEntity<Boolean> checkAdmin(@PathVariable String roomCode, @RequestBody Map<String, String> payload) {
        Room r = roomManager.getRoomOrThrow(roomCode);
        boolean isAdmin = r.getAdminHostToken().equals(payload.get("token"));
        return ResponseEntity.ok(isAdmin);
    }


}
