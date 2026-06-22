package com.las.test_quiz.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateQuestionDTO {
    private String text;
    private List<String> options;
    private String answer;
}
