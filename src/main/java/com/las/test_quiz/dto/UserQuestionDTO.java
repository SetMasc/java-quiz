package com.las.test_quiz.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserQuestionDTO {
    private Long questionId;
    private String text;
    private List<String> options;
}
