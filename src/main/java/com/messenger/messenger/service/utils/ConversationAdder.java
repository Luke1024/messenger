package com.messenger.messenger.service.utils;

import com.messenger.messenger.model.entity.Conversation;
import com.messenger.messenger.model.entity.ConversationStatus;
import com.messenger.messenger.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class ConversationAdder {

    @Autowired
    private ConversationDuplicationDetector duplicatorDetector;

    public boolean addConversation(
            User userCreating,
            List<User> usersForConversationCreation,
            List<Conversation> conversations){

        if(usersForConversationCreation.isEmpty()) return false;

        usersForConversationCreation.add(userCreating);

        if( ! isAllUsersUnique(usersForConversationCreation)) return false;

        boolean direct = checkIfDirectConversationBetweenUsers(usersForConversationCreation);

        if(isConversationDuplicated(usersForConversationCreation, conversations)) return false;

        Conversation newConversation =
                new Conversation(
                        generateId(conversations),
                        usersForConversationCreation,
                        direct);

        propagateConversationToUsers(newConversation,newConversation.getUsersInConversation());
        conversations.add(newConversation);
        return true;
    }

    private boolean isAllUsersUnique(List<User> usersForConversationCreation){
        Set<User> uniqueUsers = new HashSet<>();
        uniqueUsers.addAll(usersForConversationCreation);
        return usersForConversationCreation.size() == uniqueUsers.size();
    }

    private boolean checkIfDirectConversationBetweenUsers(List<User> usersForConversationCreation){
        return  usersForConversationCreation.size()==2;
    }

    private long generateId(List<Conversation> conversations){
        if(conversations.isEmpty()) return 0;
        else return conversations.get(conversations.size()-1).getId() + 1;
    }

    private boolean isConversationDuplicated(List<User> usersForConversationCreation, List<Conversation> conversations){
        return duplicatorDetector.isConversationWithTheSameUserSquadAlreadyExist(usersForConversationCreation, conversations);
    }

    private void propagateConversationToUsers(Conversation newConversation, List<User> usersInConversation){
        usersInConversation.stream()
                .forEach(user -> user.getConversations().put(newConversation, new ConversationStatus()));
    }
}
