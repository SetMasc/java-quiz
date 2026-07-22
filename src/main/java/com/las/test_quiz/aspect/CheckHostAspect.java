package com.las.test_quiz.aspect;

import com.las.test_quiz.annotation.CheckHost;
import com.las.test_quiz.exception.ForbiddenActionException;
import com.las.test_quiz.model.Room;
import com.las.test_quiz.service.QuizRoomManager;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Aspect
@Component
@RequiredArgsConstructor
public class CheckHostAspect {
    private final QuizRoomManager roomManager;

    @Before("@annotation(CheckHost)")
    public void checkHost(JoinPoint joinPoint){
        Object[] args = joinPoint.getArgs();
        String roomCode = (String) args[0];
        String token = (String) args[1];

        Optional<Room> resp = roomManager.findRoom(roomCode);
        if(resp.isPresent()){
            Room room = resp.get();
            if(!room.getAdminHostToken().equals(token)){
                throw new ForbiddenActionException("Only host can perform this action");
            }
        }
    }

}
