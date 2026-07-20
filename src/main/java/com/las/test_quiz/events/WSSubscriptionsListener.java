package com.las.test_quiz.events;

import com.las.test_quiz.dto.RoomDTO;
import com.las.test_quiz.model.Room;
import com.las.test_quiz.model.RoomStatus;
import com.las.test_quiz.service.QuizRoomManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class WSSubscriptionsListener {
    private final SimpMessagingTemplate messagingTemplate;
    private final QuizRoomManager roomManager;

    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event){
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = headerAccessor.getDestination();

        if(destination != null && destination.startsWith("/topic/room/")){
            String roomCode = destination.substring("/topic/room/".length());


            log.info("Got WS subscription to room {roomCode}", roomCode);

            try{
                Room currentRoom = roomManager.getRoom(roomCode);
                if(currentRoom != null){
                    messagingTemplate.convertAndSend(destination, RoomDTO.builder()
                                    .roomCode(currentRoom.getRoomCode())
                                    .users(roomManager.getUsersInRoom(currentRoom.getRoomCode()))
                                    .status(currentRoom.getStatus())
                                    .build()
                    );

                    log.info("Broadcast to {} room's topic", roomCode);
                }else{
                    log.warn("Room {} not found", roomCode);
                    messagingTemplate.convertAndSend(destination, RoomDTO.builder()
                            .status(RoomStatus.valueOf("CLOSED"))
                            .build()
                    );
                }
            }catch (Exception e){
                log.error("Error in room {}: ", roomCode, e);
            }
        }

    }
}
