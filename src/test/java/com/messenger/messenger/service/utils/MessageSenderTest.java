package com.messenger.messenger.service.utils;

import com.messenger.messenger.model.dto.SendMessageDto;
import com.messenger.messenger.model.entity.Conversation;
import com.messenger.messenger.model.entity.ConversationStatus;
import com.messenger.messenger.model.entity.User;
import com.messenger.messenger.service.utils.messagesender.MessageSender;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MessageSenderTest {

    class DataHolder {
        User tom;
        User bob;
        User rob;

        Conversation main;
        Conversation empty;

        public DataHolder(User tom, User bob, User rob, Conversation main, Conversation empty) {
            this.tom = tom;
            this.bob = bob;
            this.rob = rob;
            this.main = main;
            this.empty = empty;
        }
    }

    @Autowired
    private MessageSender messageSender;

    public DataHolder createData(){
        User tom = new User("Tom", "", "");
        tom.setId(0);
        User bob = new User("Bob", "","");
        bob.setId(1);
        User rob = new User("Rob", "", "");
        rob.setId(2);

        List<User> usersInMainConversation = new ArrayList<>();
        usersInMainConversation.addAll(Arrays.asList(tom, bob, rob));

        Conversation mainConversation = new Conversation(0, usersInMainConversation,false);
        Conversation emptyConversation = new Conversation(1, Arrays.asList(tom, rob), true);

        for(User user : usersInMainConversation){
            ConversationStatus mainStatus = new ConversationStatus();
            mainStatus.setSomethingChanged(false);
            mainStatus.setNotificationCount(0);

            ConversationStatus emptyStatus = new ConversationStatus();
            emptyStatus.setSomethingChanged(false);
            emptyStatus.setNotificationCount(0);

            user.getConversations().put(mainConversation, mainStatus);
            user.getConversations().put(emptyConversation, emptyStatus);
        }
        return new DataHolder(tom,bob,rob,mainConversation,emptyConversation);
    }

    @Test
    public void testSendingMessage(){
        DataHolder data = createData();

        String message = "Hello everyone.";
        String message2 = "Hi there";

        messageSender.send(data.tom, new SendMessageDto(0, message), data.main);

        ConversationStatus tomMainStatus = data.tom.getConversations().get(data.main);
        ConversationStatus bobMainStatus = data.bob.getConversations().get(data.main);
        ConversationStatus robMainStatus = data.rob.getConversations().get(data.main);

        Assert.assertTrue(tomMainStatus.isThereSomethingNew());
        Assert.assertTrue(tomMainStatus.getWaitingMessages().size()==1);
        //user who send the message should not to receive notification
        Assert.assertTrue(tomMainStatus.getNotificationCount()==0);

        Assert.assertTrue(bobMainStatus.isThereSomethingNew());
        Assert.assertTrue(bobMainStatus.getWaitingMessages().size()==1);
        Assert.assertTrue(bobMainStatus.getNotificationCount()==1);

        Assert.assertTrue(robMainStatus.isThereSomethingNew());
        Assert.assertTrue(robMainStatus.getWaitingMessages().size()==1);
        Assert.assertTrue(robMainStatus.getNotificationCount()==1);

        //continue conversation
        messageSender.send(data.bob, new SendMessageDto(0, message2), data.main);

        Assert.assertTrue(tomMainStatus.isThereSomethingNew());
        Assert.assertTrue(tomMainStatus.getWaitingMessages().size()==2);
        Assert.assertTrue(tomMainStatus.getNotificationCount()==1);

        Assert.assertTrue(bobMainStatus.isThereSomethingNew());
        Assert.assertTrue(bobMainStatus.getWaitingMessages().size()==2);
        Assert.assertTrue(bobMainStatus.getNotificationCount()==1);

        Assert.assertTrue(robMainStatus.isThereSomethingNew());
        Assert.assertTrue(robMainStatus.getWaitingMessages().size()==2);
        Assert.assertTrue(robMainStatus.getNotificationCount()==2);
    }
}