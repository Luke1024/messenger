package com.messenger.messenger.service.utils;

import com.messenger.messenger.model.dto.ConversationStatusDto;
import com.messenger.messenger.model.entity.Conversation;
import com.messenger.messenger.model.entity.ConversationStatus;
import com.messenger.messenger.model.entity.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MessageAcquirerTest {

    class DataHolder {
        User tom;
        User bob;
        User rob;

        Conversation main;

        public DataHolder(User tom, User bob, User rob, Conversation main) {
            this.tom = tom;
            this.bob = bob;
            this.rob = rob;
            this.main = main;
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

        for(User user : usersInMainConversation){
            ConversationStatus mainStatus = new ConversationStatus();
            mainStatus.setSomethingChanged(false);
            mainStatus.setNotificationCount(0);

            ConversationStatus emptyStatus = new ConversationStatus();
            emptyStatus.setSomethingChanged(false);
            emptyStatus.setNotificationCount(0);

            user.getConversations().put(mainConversation, mainStatus);
        }
        return new DataHolder(tom,bob,rob,mainConversation);
    }

    @Test
    public void testIsStatusChanged(){
        DataHolder data = createData();

        //starting point
        Assert.assertFalse(messageAcquirer.isStatusChanged(data.tom));

        //change status in Tom conversation
        data.tom.getConversations().get(data.main).setSomethingChanged(true);

        Assert.assertTrue(messageAcquirer.isStatusChanged(data.tom));
        Assert.assertFalse(messageAcquirer.isStatusChanged(data.rob));
    }

    @Test
    public void getStatusDtoList(){
        DataHolder data = createData();

        List<ConversationStatusDto> conversationStatusDtos = messageAcquirer.getStatusDtoList(data.tom);


        Assert.assertTrue(conversationStatusDtos.size()==1);

        String shouldBeStringRepresentation = "";
        Assert.assertEquals(shouldBeStringRepresentation,
                conversationStatusDtos.get(0).toString());
    }
}