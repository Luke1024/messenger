package com.messenger.messenger.service.utils;

import com.messenger.messenger.model.entity.Conversation;
import com.messenger.messenger.model.entity.ConversationStatus;
import com.messenger.messenger.model.entity.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ConversationAdderTest {

    @Autowired
    private ConversationAdder conversationAdder;

    @Test
    public void addConversation(){
        User tom = new User("Tom", "", "");
        tom.setId(0);
        User bob = new User("Bob", "","");
        bob.setId(1);
        User rob = new User("Rob", "", "");
        rob.setId(2);
        User martin = new User("Martin", "", "");
        martin.setId(3);

        List<Conversation> conversationDatabase = new ArrayList<>();

        List<User> usersForConversationCreation = new ArrayList<>();
        usersForConversationCreation.addAll(Arrays.asList(bob, rob));

        Assert.assertTrue(conversationAdder.addConversation(tom, usersForConversationCreation, conversationDatabase).isStatus());

        //conversation added
        Assert.assertTrue(conversationDatabase.size()==1);
        Assert.assertFalse(conversationDatabase.get(0).isDirect());


        //conversation created should be contained in every user
        Conversation conversationCreated = conversationDatabase.get(0);
        for(User user : conversationCreated.getUsersInConversation()){
            for(Map.Entry<Conversation, ConversationStatus> conversationMapEntry : user.getConversations().entrySet()){
                Assert.assertTrue(conversationMapEntry.getKey()==conversationCreated);
            }
        }

        //create conversasion with the same composition once more
        Assert.assertFalse(conversationAdder.addConversation(tom, usersForConversationCreation, conversationDatabase).isStatus());

        //create user with the same user creating and requesting conversation creation
        List<User> selfConversation = new ArrayList<>();
        selfConversation.add(tom);

        Assert.assertFalse(conversationAdder.addConversation(tom, selfConversation, conversationDatabase).isStatus());

        //second example of duplicate user
        List<User> multipliedUserConversation = new ArrayList<>();
        multipliedUserConversation.addAll(Arrays.asList(tom, rob, rob, martin));

        Assert.assertFalse(conversationAdder.addConversation(bob, multipliedUserConversation, conversationDatabase).isStatus());

        List<User> directConversation = new ArrayList<>(Arrays.asList(martin));

        //create direct conversation
        Assert.assertTrue(conversationAdder.addConversation(bob, directConversation, conversationDatabase).isStatus());
        Assert.assertTrue(conversationDatabase.get(1).isDirect());

        for(int i=0; i<conversationDatabase.size(); i++){
            Assert.assertTrue(conversationDatabase.get(i).getId()==i);
        }
    }
}