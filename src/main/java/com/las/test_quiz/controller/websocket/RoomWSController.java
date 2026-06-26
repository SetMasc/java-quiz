package com.las.test_quiz.controller.websocket;

import com.las.test_quiz.dto.RoomDTO;
import com.las.test_quiz.model.Room;
import com.las.test_quiz.service.QuizRoomManager;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class RoomWSController {
    private final SimpMessagingTemplate messagingTemplate;

    private final QuizRoomManager roomManager;

    @MessageMapping("/rooms/{roomCode}/newUser")
    @SendToUser("/queue/token")
    public Map<String, String> addUser(Map<String, String> payload,
                                        @DestinationVariable String roomCode){
        String username = null;
        String token = null;
        if (payload != null){
            username = payload.get("username");
            token = payload.get("userToken");
        }

        Map<String, String> result = roomManager.addUserToRoom(roomCode, username, token);

        if (!result.containsKey("error")) {
            Room r = roomManager.getRoom(roomCode);
            RoomDTO roomDTO = RoomDTO.builder()
                    .roomCode(r.getRoomCode())
                    .users(roomManager.getUsersInRoom(r.getRoomCode()))
                    .status(r.getStatus())
                    .build();

            messagingTemplate.convertAndSend("/topic/room/" + roomCode, roomDTO);
        }

        return result;
    }

    @MessageMapping("/rooms/{roomCode}/deleteUser")
    public Map<String, String> deleteUser(Map<String, String> payload,
                                       @DestinationVariable String roomCode){
        String token = null;
        if (payload != null){
            token = payload.get("userToken");
        }

        Map<String, String> result = roomManager.deleteUserFromRoom(roomCode, token);

        if (!result.containsKey("error")) {
            Room r = roomManager.getRoom(roomCode);
            Object response;
            if(r != null){
                response = RoomDTO.builder()
                        .roomCode(r.getRoomCode())
                        .users(roomManager.getUsersInRoom(r.getRoomCode()))
                        .status(r.getStatus())
                        .build();
            }else{
                response = Map.of("status", "CLOSED");
            }
            messagingTemplate.convertAndSend("/topic/room/" + roomCode, response);
        }

        return result;
    }
}
