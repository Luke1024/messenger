package com.messenger.messenger.service.utils;

import com.messenger.messenger.model.entity.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MessageAcquirerTest {

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
    private MessageAcquirer messageAcquirer;

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
    public void isStatusChanged(){
        DataHolder data = createData();

        //starting point
        Assert.assertFalse(messageAcquirer.isStatusChanged(data.tom));

        //change status in Tom conversation
        data.tom.getConversations().get(data.main).setSomethingChanged(true);

        Assert.assertTrue(messageAcquirer.isStatusChanged(data.tom));
        Assert.assertFalse(messageAcquirer.isStatusChanged(data.rob));
    }

    @Test
    public void getNewMessages(){
        DataHolder data = createData();

        //should be no messages
        Assert.assertTrue(messageAcquirer.getNewMessages(data.tom, 0).size()==0);

        //sending message to "empty" conversation
        data.tom.getConversations().get(data.empty).getWaitingMessages().add(new Message(
                "Hello, hello.", LocalDateTime.now(), data.rob, data.empty));

        Assert.assertTrue(messageAcquirer.getNewMessages(data.tom,0).size()==0);
        Assert.assertTrue(messageAcquirer.getNewMessages(data.tom,1).size()==1);
    }

    @Test
    public void loadLastBatch(){
        DataHolder data = createData();

        MessageBatchDay messageBatchDay1 = new MessageBatchDay(0);
        MessageBatchDay messageBatchDay2 = new MessageBatchDay(1);

        data.main.getMessageBatchDays().addAll(Arrays.asList(messageBatchDay1, messageBatchDay2));

        Optional<MessageBatchDay> messageBatchOptional = messageAcquirer.loadLastBatch(data.tom,0);

        Assert.assertTrue(messageBatchOptional.get().getId()==1);
    }

    @Test
    public void loadBatch(){
        DataHolder data = createData();

        MessageBatchDay messageBatchDay2 = new MessageBatchDay(1);

        data.main.getMessageBatchDays().addAll(Arrays.asList(messageBatchDay2));

        Assert.assertEquals(0,messageAcquirer.loadBatch(data.tom,0,0).get().getId());
        Assert.assertEquals(1,messageAcquirer.loadBatch(data.tom,0,1).get().getId());
    }
}