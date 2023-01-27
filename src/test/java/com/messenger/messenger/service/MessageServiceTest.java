package com.messenger.messenger.service;

import com.messenger.messenger.model.dto.RequestDto;
import com.messenger.messenger.model.dto.SendMessageDto;
import com.messenger.messenger.model.entity.Message;
import com.messenger.messenger.model.entity.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    User newUser1 = new User("TestUser1", "password1", "key");
    User newUser2 = new User("TestUser2", "password2", "key2");
    User newUser3 = new User("TestUser3", "password3", "key3");

    @Test
    public void integratedUsageScenarioTest() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        //creating users and adding them to database
        newUser1.setId(0);
        newUser2.setId(1);
        newUser3.setId(2);

        Field usersField = userService.getClass().getDeclaredField("users");
        usersField.setAccessible(true);
        usersField.set(userService, new ArrayList<>(Arrays.asList(newUser1, newUser2, newUser3)));

        //opening access to necessary methods excluding authorization
        Method sendMessage = messageService.getClass().getDeclaredMethod("sendMessage", SendMessageDto.class, User.class);
        sendMessage.setAccessible(true);

        Method executeUpdating = messageService.getClass().getDeclaredMethod("executeUpdating", RequestDto.class, User.class);
        executeUpdating.setAccessible(true);

        Method update = messageService.getClass().getDeclaredMethod("load", RequestDto.class, HttpServletRequest.class);
        update.setAccessible(true);

        creatingConversation();
        messageSendingAndBatchSplitting(sendMessage);
        notificationTesting(sendMessage, update);
    }

    private void creatingConversation(){
        //creating conversations
        conversationService.addConversation(Arrays.asList(newUser2.getDto(), newUser3.getDto()),newUser1);
        conversationService.addConversation(Arrays.asList(newUser1.getDto(), newUser3.getDto()), newUser2);

        //check created conversations
        Assert.assertTrue(conversationService.findById(0).isPresent());
        Assert.assertTrue(conversationService.findById(1).isPresent());
    }

    private void messageSendingAndBatchSplitting(Method sendMessage) throws InvocationTargetException, IllegalAccessException {
        //newUser1 sending first message
        sendMessage.invoke(messageService,
                new SendMessageDto(0, "Hello fellow users."), newUser1);
        //is new message batch created
        Assert.assertTrue(conversationService.findById(0).get().getMessageBatch(newUser2,0).isPresent());
        //is message there
        Assert.assertEquals("Hello fellow users.", conversationService.findById(0).get().getMessageBatch(newUser2,0).get().getMessages().get(0).getContent());

        //add message count necessary to create second batch

        for(int i=0; i<settingsService.messageCountInBatch; i++){
            sendMessage.invoke(messageService,
                    new SendMessageDto(0, "I will add some content as newUser2."), newUser2);
        }
        //was second batch created?
        Assert.assertTrue(conversationService.findById(0).get().getMessageBatch(newUser3,1).isPresent());

        //add some unique message and check if it exist in second batch
        sendMessage.invoke(messageService,
                new SendMessageDto(0, "Unique message."), newUser3);

        //check if unique message exists

        List<Message> messagesInLastBatch = conversationService.findById(0).get().getMessageBatch(newUser3,1).get().getMessages();

        Assert.assertEquals("Unique message.", messagesInLastBatch.get(messagesInLastBatch.size()-1).getContent());
    }

    private void notificationTesting(Method sendMessage, Method update) throws InvocationTargetException, IllegalAccessException {
        //update with every user to reset notifications
        //update.invoke(messageService,
          //      new RequestDto(true,),
            //    newUser1);
    }
}