package com.las.test_quiz.controller.websocket;

import com.las.test_quiz.dto.UserAnswerDTO;
import com.las.test_quiz.model.User;
import com.las.test_quiz.model.Room;
import com.las.test_quiz.service.QuizRoomManager;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class QuizWSController {
    private final QuizRoomManager roomManager;
    private final SimpMessagingTemplate messagingTemplate;

    public QuizWSController(QuizRoomManager roomManager, SimpMessagingTemplate messagingTemplate) {
        this.roomManager = roomManager;
        this.messagingTemplate = messagingTemplate;
    }


    @MessageMapping("/next-question")
    @SendTo("/topic/quiz-events")
    public String broadcastQuestion(String message){
        System.out.println("Received command from client: " + message);
        return "Server broadcasts: " + message;
    }

    @MessageMapping("/room/{roomCode}/submit-answer")
    public void handleAnswer(@DestinationVariable String roomCode,
                             @Payload UserAnswerDTO answerDTO,
                             SimpMessageHeaderAccessor headerAccessor){
        String sessionId = headerAccessor.getSessionId();

        Room r = roomManager.getRoom(roomCode);
        if(r == null) return;
        User p = r.getUsers().get(sessionId);
        if(p != null){
            //todo начисление тчков и проверка на то что ответ только один
        }
        r.getStats().putIfAbsent(answerDTO.getAnswer(), answerDTO.getQuestionId());//todo
        System.out.println("User " /*todo*/ + " selects answer " + answerDTO.getAnswer() + " at room " + roomCode);
    }

}
