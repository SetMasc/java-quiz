package com.las.test_quiz.controller.http;

import com.las.test_quiz.model.Room;
import com.las.test_quiz.service.QuizRoomManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.swing.text.html.HTML;

@Controller
@RequiredArgsConstructor
public class SPAController {
    private final QuizRoomManager roomManager;

    @GetMapping(path = {"/","/invite/{roomCode}"})
    public String forwardToSpa(){
        return  "forward:/index.html";
    }
}
