package com.messenger.messenger.service;

import com.messenger.messenger.model.entity.Conversation;
import com.messenger.messenger.model.entity.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ConversationServiceTest {

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private UserService userService;

    @Test
    public void testAddingConversationAndFinding() throws NoSuchFieldException, IllegalAccessException {

        User newUser1 = new User("TestUser1", "password", "key");
        newUser1.setId(0);
        User newUser2 = new User("TestUser2", "password", "key2");
        newUser2.setId(1);

        Field usersField = userService.getClass().getDeclaredField("users");
        usersField.setAccessible(true);
        usersField.set(userService, new ArrayList<>(Arrays.asList(newUser1, newUser2)));

        conversationService.addConversation(new ArrayList<>(Arrays.asList(newUser2.getDto())), newUser1);

        Optional<Conversation> newConversationOptional = conversationService.findById(0);

        //is conversation created
        Assert.assertTrue(newConversationOptional.isPresent());

        //is user added to conversation aware
        Assert.assertTrue(newUser2.getConversations().containsKey(newConversationOptional.get()));
    }
}