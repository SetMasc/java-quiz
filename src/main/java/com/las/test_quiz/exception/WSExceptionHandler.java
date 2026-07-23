package com.las.test_quiz.exception;

import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.Map;

@ControllerAdvice
public class WSExceptionHandler {

    @MessageExceptionHandler({RoomNotFoundException.class, UserNotFoundException.class, ForbiddenActionException.class})
    @SendToUser("/queue/errors")
    public Map<String, String> handleWsException(RuntimeException ex) {
        return Map.of("error", ex.getMessage());
    }
}