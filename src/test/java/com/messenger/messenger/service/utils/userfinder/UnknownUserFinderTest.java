package com.messenger.messenger.service.utils.userfinder;

import com.messenger.messenger.model.entity.Conversation;
import com.messenger.messenger.model.entity.ConversationStatus;
import com.messenger.messenger.model.entity.User;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UnknownUserFinderTest {

    private UnknownUserFinder unknownFinder = new UnknownUserFinder();

    @Test
    public void testFindingNewUsers(){

        User userRequesting = new User("test_user", "", "");

        User user1 = new User("Martin","","");
        User user2 = new User("Tim","","");
        User user3 = new User("Rob","","");
        User user4 = new User("Bob","","");
        User user5 = new User("Sam","","");

        Conversation conversation1 = new Conversation(0, Arrays.asList(userRequesting, user1), true);
        Conversation conversation2 = new Conversation(1, Arrays.asList(userRequesting, user2), true);

        userRequesting.getConversations().put(conversation1, new ConversationStatus());
        userRequesting.getConversations().put(conversation2, new ConversationStatus());

        List<User> usersInDatabase = new ArrayList<>(Arrays.asList(user1, user2, user3, user4, user5, userRequesting));


        //find user who is already in direct conversation with user
        Assert.assertTrue(
                unknownFinder.findUsersByNameExcludingUsersAlreadyInDirectConversation("martin", userRequesting, usersInDatabase).size() == 0
        );
        //find rob and bob
        Assert.assertTrue(
                unknownFinder.findUsersByNameExcludingUsersAlreadyInDirectConversation("ob", userRequesting, usersInDatabase).size() == 2
        );
    }

}