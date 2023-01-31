package com.messenger.messenger.service;

import com.messenger.messenger.model.dto.ConversationStatusDto;
import com.messenger.messenger.model.dto.SendMessageDto;
import com.messenger.messenger.model.entity.Conversation;
import com.messenger.messenger.model.entity.Message;
import com.messenger.messenger.model.entity.User;
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

        creatingConversation();
        messageSendingAndBatchSplitting();
        notificationTesting();
    }

    private void creatingConversation(){
        //creating conversations
        messageService.addConversation(newUser1 ,Arrays.asList(newUser2.getDto(), newUser3.getDto()));
        messageService.addConversation(newUser2, Arrays.asList(newUser1.getDto(), newUser3.getDto()));

        //check created conversations
        Assert.assertTrue(conversationService.findById(0).isPresent());
        Assert.assertTrue(conversationService.findById(1).isPresent());
    }

    private void messageSendingAndBatchSplitting() throws InvocationTargetException, IllegalAccessException {
        //newUser1 sending first message
        messageService.send(
                newUser1,
                new SendMessageDto(0, "Hello fellow users."));
        //is new message batch created
        Assert.assertTrue(conversationService.findById(0).get().getMessageBatch(newUser2,0).isPresent());
        //is message there
        Assert.assertEquals("Hello fellow users.", conversationService.findById(0).get().getMessageBatch(newUser2,0).get().getMessages().get(0).getContent());

        //add message count necessary to create second batch

        for(int i=0; i<settingsService.messageCountInBatch; i++){
            messageService.send(
                    newUser2,
                    new SendMessageDto(0, "I will add some content as newUser2."));
        }
        //was second batch created?
        Assert.assertEquals(1, messageService.loadLastBatch(newUser1, 0).get().getId());

        //add some unique message and check if it exist in second batch
        messageService.send(
                newUser3,
                new SendMessageDto(0, "Unique message."));

        //check if unique message exists

        List<Message> messagesInLastBatch = conversationService.findById(0).get().getMessageBatch(newUser3,1).get().getMessages();

        Assert.assertEquals("Unique message.", messagesInLastBatch.get(messagesInLastBatch.size()-1).getContent());
    }

    private void notificationTesting()
            throws InvocationTargetException, IllegalAccessException {

            List<User> users = new ArrayList<>(Arrays.asList(newUser1, newUser2, newUser3));

            List<Conversation> createdConversations = new ArrayList<>(Arrays.asList(
                    conversationService.findById(0).get(),
                    conversationService.findById(1).get()
            ));

            //all user requesting last batch in every conversation to reset conversation statuses
            for(User user : users){
                for(Conversation conversation : createdConversations){
                   messageService.loadLastBatch(user, conversation.getId());
                }
            }

            //waiting message count in conversation status should be 0
            for(User user : users){
                for(ConversationStatusDto conversationStatusDto : messageService.getConversationStatus(user)){
                    Assert.assertTrue(conversationStatusDto.getWaitingMessages() == 0 );
                }
            }

            //status for user change should be false
            for(User user : users){
                Assert.assertFalse(messageService.isStatusChanged(user));
            }

            //new user1 writing something in conversation id 0
            messageService.send(newUser1, new SendMessageDto(0, "Hello again fellow users. I'm, still here."));

            Assert.assertTrue(messageService.isStatusChanged(newUser1));
            Assert.assertTrue(messageService.isStatusChanged(newUser2));
            Assert.assertTrue(messageService.isStatusChanged(newUser3));

            //emulate newUser2 client
            Assert.assertTrue(messageService.isStatusChanged(newUser2));

            List<ConversationStatusDto> conversationStatusDtos =
                    messageService.getConversationStatus(newUser2).stream().filter(
                            conversationStatusDto -> conversationStatusDto.getConversationId()==0).collect(Collectors.toList());


            Assert.assertEquals( 1 , conversationStatusDtos.get(0).getWaitingMessages());
            Assert.assertEquals("Hello again fellow users. I'm, still here.",
                    messageService.getNewMessages(newUser2,0).get(0).getContent());
    }
}