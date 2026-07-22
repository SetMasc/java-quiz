package com.las.test_quiz.service;

import com.las.test_quiz.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class QuizUserManagerTest {
    private QuizUserManager userManager;

    @BeforeEach
    void setUp(){
        userManager = new QuizUserManager();
    }

    @Test
    void createUser_setsUsernameAndZeroScore(){
        User user = userManager.createUser("User1");

        assertThat(user.getUsername()).isEqualTo("User1");
        assertThat(user.getScore()).isZero();
        assertThat(user.getToken()).isNotNull();
    }

    @Test
    void createUser_generateUniqueToken(){
        User user1 = userManager.createUser("User1");
        User user2 = userManager.createUser("User2");

        assertThat(user1.getToken()).isNotEqualTo(user2.getToken());
    }

    @Test
    void findUser_withValidToken_returnsUser(){
        User user = userManager.createUser("User1");
        assertThat(userManager.findUser(user.getToken())).contains(user);
    }

    @Test
    void findUser_withNullToken_returnsEmpty(){
        assertThat(userManager.findUser(null)).isEmpty();
    }

    @Test
    void findUser_withUnknownToken_returnsEmpty(){
        assertThat(userManager.findUser("not-exists-token")).isEmpty();
    }

    @Test
    void deleteUser_removesUserFromActiveUsers(){
        User user = userManager.createUser("User1");

        userManager.deleteUser(user.getToken());

        assertThat(userManager.findUser(user.getToken())).isEmpty();
    }

}
