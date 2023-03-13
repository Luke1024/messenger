package com.messenger.messenger.service.utils;

import com.messenger.messenger.model.entity.Conversation;
import com.messenger.messenger.model.entity.User;
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
public class ConversationDuplicationDetectorTest {

    @Autowired
    private ConversationDuplicationDetector conversationDuplicationDetector;

    @Test
    public void isConversationWithTheSameUserSquadAlreadyExist() {

        List<Conversation> database1 = new ArrayList<>();

        User user1 = new User("Tom", "", "");
        User user2 = new User("Rob", "", "");
        User user3 = new User("Bob", "", "");
        User user4 = new User("Tim", "", "");

        Conversation conversationInDatabase1 =
                new Conversation(0, Arrays.asList(user1, user2, user3) ,false);
        Conversation conversationInDatabase2 =
                new Conversation(1, Arrays.asList(user1, user2), true);

        database1.addAll(Arrays.asList(conversationInDatabase1, conversationInDatabase2));

        //identical conversation
        Assert.assertTrue(conversationDuplicationDetector.isConversationWithTheSameUserSquadAlreadyExist(
                Arrays.asList(user1, user2, user3),database1));

        //conversation with diferrent order of users

        Conversation conversationInDatabase3 =
                new Conversation(0, Arrays.asList(user3, user1, user2),false);
        List<Conversation> database2 = new ArrayList<>(Arrays.asList(conversationInDatabase3));

        Assert.assertTrue(conversationDuplicationDetector.isConversationWithTheSameUserSquadAlreadyExist(
                Arrays.asList(user1, user2, user3), database2)
        );

        //different user squad
        Assert.assertFalse(conversationDuplicationDetector.isConversationWithTheSameUserSquadAlreadyExist(
                Arrays.asList(user1, user2, user4), database2)
        );
    }
}