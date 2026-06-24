package com.las.test_quiz.aspect;

import com.las.test_quiz.model.Room;
import com.las.test_quiz.service.QuizRoomManager;
import com.las.test_quiz.service.QuizUserManager;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Aspect
@Component
public class UserAffiliationAspect {
    private final QuizUserManager userManager;
    private final QuizRoomManager roomManager;

    public UserAffiliationAspect(QuizRoomManager roomManager, QuizUserManager userManager){
        this.roomManager = roomManager;
        this.userManager = userManager;
    }

    @Before("@annotation(com.las.test_quiz.annotation.CheckAffiliation)")
    public void verifyUserAffiliation(JoinPoint joinPoint){
        Object[] args = joinPoint.getArgs();

        String userToken = null;


        for(Object arg : args){
            if(arg == null) continue;

            if(arg instanceof Map<?, ?> payloadMap){
                Object tokenValue = payloadMap.get("user_token");
                if(tokenValue != null){
                    userToken = tokenValue.toString();
                }
            }
            continue;
        }

        if(userToken == null){
            System.out.println("Field \"user_token\" not fount by aspect");
        }else{
            List<Room> rooms = new ArrayList<>();
            roomManager.getAllRooms().forEach((s, room) -> rooms.add(room));

            //todo возмоно не здесь
            for(Room room : rooms){
                if(room.getAdminHostToken().equals(userToken)){
                    System.out.println("User is already in other room and he's an admin!");
//                    if(room.getRoomCode().equals(roomCode)){
//                        System.out.println("It's this rooms's admin");
//                    }
                }
                if(room.getUsers().containsKey(userToken)){
                    System.out.println("User is already in other room");
                }
            }
        }






    }
}
