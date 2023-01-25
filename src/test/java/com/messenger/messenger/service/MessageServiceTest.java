package com.messenger.messenger.service;

import com.messenger.messenger.model.dto.MessageDto;
import com.messenger.messenger.model.dto.RequestDto;
import com.messenger.messenger.model.entity.Conversation;
import com.messenger.messenger.model.entity.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MessageServiceTest {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private SettingsService settingsService;

    @Test
    public void integratedUsageScenarioTest() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        //creating users and adding them to database
        User newUser1 = new User("TestUser1", "password1", "key");
        newUser1.setId(0);
        User newUser2 = new User("TestUser2", "password2", "key2");
        newUser2.setId(1);
        User newUser3 = new User("TestUser3", "password3", "key3");
        newUser3.setId(2);

        Field usersField = userService.getClass().getDeclaredField("users");
        usersField.setAccessible(true);
        usersField.set(userService, new ArrayList<>(Arrays.asList(newUser1, newUser2, newUser3)));

        //opening access to necessary methods excluding authorization
        Method sendMessage = messageService.getClass().getDeclaredMethod("sendMessage", MessageDto.class, User.class);
        sendMessage.setAccessible(true);

        Method executeUpdating = messageService.getClass().getDeclaredMethod("executeUpdating", RequestDto.class, User.class);
        executeUpdating.setAccessible(true);

        //creating conversations
        conversationService.addConversation(Arrays.asList(newUser2.getDto(), newUser3.getDto()),newUser1);
        conversationService.addConversation(Arrays.asList(newUser1.getDto(), newUser3.getDto()), newUser2);

        //check created conversations
        Assert.assertTrue(conversationService.findById(0).isPresent());
        Assert.assertTrue(conversationService.findById(1).isPresent());

        //newUser1 sending first message
        sendMessage.invoke(messageService,
                new MessageDto(0,0, LocalDateTime.now(),"Hello fellow users."), newUser1);
        //is new message batch created
        Assert.assertTrue(conversationService.findById(0).get().getMessageBatch(newUser2,0).isPresent());
        //is message there
        Assert.assertEquals("Hello fellow users.", conversationService.findById(0).get().getMessageBatch(newUser2,0).get().getMessages().get(0).getContent());
        //add message count necessary to create second batch
        //sendMessage.invoke(messageService,
                //new MessageDto())

        //is new message
        //Assert.assertEquals("Hello fellow users.", conversationService.findById(0).get().getMessageBatch(newUser2, 0).get().getMessages().get(0).getContent());

        //access method

    }
}