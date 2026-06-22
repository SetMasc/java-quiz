package com.las.test_quiz.dto;

import lombok.Data;

@Data
public class UserAnswerDTO {
    private Long questionId;
    private String answer;
}
