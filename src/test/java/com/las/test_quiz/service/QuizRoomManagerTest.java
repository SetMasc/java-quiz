package com.las.test_quiz.service;

import com.las.test_quiz.model.Room;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class QuizRoomManagerTest {

    private QuizRoomManager roomManager;

    @BeforeEach
    void setUp(){
        roomManager = new QuizRoomManager(new QuizUserManager());
    };


    @Test
    void createRoom_generatesSixDigitCode(){
        Room room = roomManager.createRoom();

        assertThat(room.getRoomCode()).hasSize(6);
    }

    @Test
    void addUserToRoom_firstUserBecomesHost(){
        Room room = roomManager.createRoom();

        var response = roomManager.addUserToRoom(room.getRoomCode(), "User1", null);

        assertThat(room.getAdminHostToken()).isEqualTo(response.get("token"));
    }

    @Test
    void addUserToRoom_withNullToken_doesNotThrow(){
        Room room = roomManager.createRoom();

        assertThat(roomManager.addUserToRoom(room.getRoomCode(), "User1", null).containsKey("token"));
    }

    @Test
    void addUserToRoom_roomNotFound_returnsError(){
        var response = roomManager.addUserToRoom("000000", "User1", null);

        assertThat(response.containsKey("error"));
    }

    @Test
    void addUserToRoom_secondUser_doesNotBecomeHost(){
        Room room = roomManager.createRoom();
        var first = roomManager.addUserToRoom(room.getRoomCode(), "User1", null);

        roomManager.addUserToRoom(room.getRoomCode(), "User2", null);

        assertThat(room.getAdminHostToken()).isEqualTo(first.get("token"));
    }

    @Test
    void deleteUserFromRoom_hostLeaves_closeRoomAndRemovesAllUsers(){
        Room room = roomManager.createRoom();
        var host = roomManager.addUserToRoom(room.getRoomCode(), "Admin", null);
        roomManager.addUserToRoom(room.getRoomCode(), "User", null);

        roomManager.deleteUserFromRoom(room.getRoomCode(), host.get("token"));

        assertThat(roomManager.findRoom(room.getRoomCode())).isEmpty();
    }

    @Test
    void deleteUserFromRoom_userLeaves_onlyRemovesThatUser(){
        Room room = roomManager.createRoom();
        roomManager.addUserToRoom(room.getRoomCode(), "Admin", null);
        var user = roomManager.addUserToRoom(room.getRoomCode(), "User", null);

        roomManager.deleteUserFromRoom(room.getRoomCode(), user.get("token"));

        assertThat(roomManager.getUsersInRoom(room.getRoomCode()).size()).isEqualTo(1);
    }

    @Test
    void deleteUserFromRoom_roomNotFound_returnsError(){
        var response = roomManager.deleteUserFromRoom("000000", "any-token");

        assertThat(response.containsKey("error"));
    }

    @Test
    void deleteUserFromRoom_nullToken_returnsError(){
        Room room = roomManager.createRoom();

        var response = roomManager.deleteUserFromRoom(room.getRoomCode(), null);

        assertThat(response.containsKey("error"));
    }

    @Test
    void getUsersInRoom_unknownRoom_returnsEmptyList() {
        assertThat(roomManager.getUsersInRoom("000000").isEmpty());
    }

    @Test
    void getUsersInRoom_userMappingToDTO_returnsDTO(){
        Room room = roomManager.createRoom();
        roomManager.addUserToRoom(room.getRoomCode(), "User1", null);

        var users = roomManager.getUsersInRoom(room.getRoomCode());
        var user = users.get(0);

        assertThat(user.score()).isZero();
        assertThat(user.userId()).isNotNull();
        assertThat(user.username()).isEqualTo("User1");
    }
}
