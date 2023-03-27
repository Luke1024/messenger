package com.messenger.messenger.service.utils.userfinder;

import com.messenger.messenger.model.entity.Conversation;
import com.messenger.messenger.model.entity.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

class UnknownUserFinder {

    public List<User> findUsersByNameExcludingUsersAlreadyInDirectConversation(String userName, User userRequesting, List<User> users){
        List<User> usersFoundInDatabase = findUsersByStringContainedInTheName(userName, users);
        List<User> usersInDirectConversationWithUserRequesting = findUsersInDirectConversationWithUserRequesting(userRequesting);
        return removeUsersInDirectConversationFromUsersFoundInDatabase(usersFoundInDatabase, usersInDirectConversationWithUserRequesting);
    }

    private List<User> findUsersByStringContainedInTheName(
            String userName, List<User> users){

        List<User> usersCompatible = new ArrayList<>();
        for(User user : users){
            if(user.getName().toLowerCase().contains(userName.toLowerCase())) {
                usersCompatible.add(user);
            }
        }
        return usersCompatible;
    }

    private List<User> findUsersInDirectConversationWithUserRequesting(User userRequesting){
        List<Conversation> directConversations  = userRequesting.getConversations().entrySet().stream().map(entry -> entry.getKey())
                .filter(conversation -> conversation.isDirect()).collect(Collectors.toList());
        return directConversations.stream()
                .map(conversation -> conversation.getUsersInConversation())
                .flatMap(Collection::stream).filter(user -> user != userRequesting)
                .collect(Collectors.toList());
    }

    private List<User> removeUsersInDirectConversationFromUsersFoundInDatabase(List<User> usersFoundInDatabase, List<User> usersInDirectConversation){
        usersFoundInDatabase.removeAll(usersInDirectConversation);
        return usersFoundInDatabase;
    }
}
