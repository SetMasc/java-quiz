package com.las.test_quiz.aspect;

import com.las.test_quiz.service.QuizRoomManager;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class RoomSecurityAspect {
    private QuizRoomManager roomManager;



}
