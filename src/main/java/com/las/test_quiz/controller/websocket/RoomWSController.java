package com.las.test_quiz.controller.websocket;

import com.las.test_quiz.model.Room;
import com.las.test_quiz.model.User;
import com.las.test_quiz.service.QuizRoomManager;
import com.las.test_quiz.service.QuizUserManager;
import lombok.NonNull;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

@Controller
public class RoomWSController {
    private final QuizRoomManager roomManager;
    private final QuizUserManager userManager;

    public RoomWSController(QuizRoomManager roomManager, QuizUserManager userManager) {
        this.roomManager = roomManager;
        this.userManager = userManager;
    }

    @MessageMapping("/rooms/{roomCode}/join")
    @SendToUser("/queue/token")
    public Map<String, String> joinRoom(SimpMessageHeaderAccessor headerAccessor,
                                        Map<String, String> payload,
                                        @DestinationVariable String roomCode){
        String sessionId = headerAccessor.getSessionId();
        String username = null;
        String token = null;
        try{
            username = payload.get("username");
            token = payload.get("user_token");
        }catch (Exception ignored){}

        User u;
        if(token != null && userManager.getAllUsers().containsKey(token)){
            u = userManager.getUser(token);
        }else{
            u = userManager.createUser(sessionId, username);
        }

        Map<String, String> response = new HashMap<>();
        response.put("token", u.getToken());

        Room r = roomManager.getRoom(roomCode);
        boolean isHost = false;

        if(r.getAdminHostToken() == null){
            r.setAdminHostToken(u.getToken());
            isHost = true;
        }

        if(r.getAdminHostToken().equals(u.getToken())){
            isHost = true;
        }

        response.put("isHost", String.valueOf(isHost));

        if(r.getUsers().putIfAbsent(u.getToken(), u) == null){
            System.out.println("Player " + u + " join to room " + r);
        }
        response.put("room_status", r.getStatus().toString());

        return response;
    }
}
