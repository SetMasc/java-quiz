package com.las.test_quiz.controller.rest;

import com.las.test_quiz.service.QuizRoundService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/game")
public class QuizController {
    private QuizRoundService roundService;

    public QuizController(QuizRoundService roundService){
        this.roundService = roundService;
    }

    @GetMapping("/start")
    public String startGame(){
        System.out.println("start");
        while (roundService.tick()){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return "Game started 5 sec ago";
    }
}
