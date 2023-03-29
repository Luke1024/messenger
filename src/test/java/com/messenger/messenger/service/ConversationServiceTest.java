package com.messenger.messenger.service;

import com.messenger.messenger.model.dto.ConversationStatusDto;
import com.messenger.messenger.model.dto.SendMessageDto;
import com.messenger.messenger.model.entity.Conversation;
import com.messenger.messenger.model.entity.User;
import com.messenger.messenger.service.utils.Settings;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ConversationServiceTest {

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private UserService userService;

    @Autowired
    private Settings settings;

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

        creatingConversation();
        messageSendingAndBatchSplitting();
        notificationTesting();
    }

    private void creatingConversation(){
        //creating conversations
        List<User> firstConversation = new ArrayList<>();
        firstConversation.add(newUser2);
        firstConversation.add(newUser3);
        Assert.assertTrue(conversationService.addConversation(newUser1 ,firstConversation).isStatus());

        List<User> secondConversation = new ArrayList<>();
        secondConversation.add(newUser1);
        secondConversation.add(newUser3);
        //creating conversation with the same users should be blocked
        Assert.assertFalse(conversationService.addConversation(newUser2, secondConversation).isStatus());

        //check created conversations
        Assert.assertTrue(conversationService.findById(0).isPresent());
    }

    private void messageSendingAndBatchSplitting() throws InvocationTargetException, IllegalAccessException {

    }

    private void notificationTesting()
            throws InvocationTargetException, IllegalAccessException {

            List<User> users = new ArrayList<>(Arrays.asList(newUser1, newUser2, newUser3));

            Conversation createdConversation = conversationService.findById(0).get();

            for(User user : users){
                conversationService.loadLastBatch(user, createdConversation.getId());
            }

            //waiting message count in conversation status should be 0
            for(User user : users){
                for(ConversationStatusDto conversationStatusDto : conversationService.getStatus(user)){
                    Assert.assertTrue(conversationStatusDto.getWaitingMessages() == 0 );
                }
            }

            //status for user change should be false
            for(User user : users){
                Assert.assertFalse(conversationService.isStatusChanged(user));
            }

            //new user1 writing something in conversation id 0
            conversationService.send(newUser1, new SendMessageDto(0, "Hello again fellow users. I'm, still here."));

            boolean isStatusChanged = conversationService.isStatusChanged(newUser1);
            Assert.assertTrue(isStatusChanged);
            Assert.assertTrue(conversationService.isStatusChanged(newUser2));
            Assert.assertTrue(conversationService.isStatusChanged(newUser3));

            //emulate newUser2 client
            Assert.assertTrue(conversationService.isStatusChanged(newUser2));

            List<ConversationStatusDto> conversationStatusDtos =
                    conversationService.getStatus(newUser2).stream().filter(
                            conversationStatusDto -> conversationStatusDto.getConversationId()==0).collect(Collectors.toList());


            Assert.assertEquals( 1 , conversationStatusDtos.get(0).getWaitingMessages());
            Assert.assertEquals("Hello again fellow users. I'm, still here.",
                    conversationService.getNewMessages(newUser2,0).get(0).getContent());
    }
}