package com.las.test_quiz.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class QuizRoundService {
    private int remainingTime = 5;
    private SimpMessagingTemplate messagingTemplate;

    public QuizRoundService(SimpMessagingTemplate messagingTemplate){
        this.messagingTemplate = messagingTemplate;
    }

    public boolean tick(){
        if(remainingTime > 0){
            remainingTime--;
            messagingTemplate.convertAndSend("/topic/timer", remainingTime);
            return true;
        }else{
            messagingTemplate.convertAndSend("/topic/quiz-events", "TIME_IS_UP");
            return false;
        }
    }

}
