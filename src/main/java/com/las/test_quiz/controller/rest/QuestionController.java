package com.las.test_quiz.controller.rest;

import com.las.test_quiz.dto.CreateQuestionDTO;
import com.las.test_quiz.model.Question;
import com.las.test_quiz.repos.QuestionRepos;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {
    private final QuestionRepos questRepos;

    public QuestionController(QuestionRepos questRepos) {
        this.questRepos = questRepos;
    }

    @PostMapping("/add")
    public Question addQuestion(@RequestBody CreateQuestionDTO dto){
        Question q = new Question();
        q.setText(dto.getText());
        q.setCorrectAnswer(dto.getAnswer());
        q.setOptions(dto.getOptions());

        return questRepos.save(q);
    }

    @GetMapping("/all")
    public List<Question> getAllQuestions(){
        return questRepos.findAll();
    }
}
