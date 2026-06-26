package com.las.test_quiz.controller.websocket;

import com.las.test_quiz.model.User;
import com.las.test_quiz.service.QuizRoomManager;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class RoomWSController {
    private final SimpMessagingTemplate messagingTemplate;

    private final QuizRoomManager roomManager;

    @MessageMapping("/rooms/{roomCode}/join")
    @SendToUser("/queue/token")
    public Map<String, String> joinRoom(Map<String, String> payload,
                                        @DestinationVariable String roomCode){
        String username = null;
        String token = null;
        if (payload != null){
            username = payload.get("username");
            token = payload.get("user_token");
        }
        Map<String, String> response = roomManager.addUserToRoom(roomCode, username, token);

        List<User> users = roomManager.getUsersInRoom(roomCode);
        messagingTemplate.convertAndSend("/topic/room/" + roomCode, users);

        return response;
    }
}
