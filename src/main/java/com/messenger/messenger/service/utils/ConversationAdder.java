package com.messenger.messenger.service.utils;

import com.messenger.messenger.model.entity.Conversation;
import com.messenger.messenger.model.entity.ConversationStatus;
import com.messenger.messenger.model.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ConversationAdder {

    @Autowired
    private ConversationDuplicationDetector duplicatorDetector;
    private Logger logger = LoggerFactory.getLogger(ConversationAdder.class);

    public boolean addConversation(
            User userCreating,
            List<User> usersForConversationCreation,
            List<Conversation> conversations){

        List<User> usersForConversationCreationCloned = new ArrayList<>();
        usersForConversationCreationCloned.addAll(usersForConversationCreation);

        if(usersForConversationCreationCloned.isEmpty()){
            logger.info("Conversation creation error: There is no users for conversation creation.");
            return false;
        }

        usersForConversationCreationCloned.add(userCreating);
        if( ! isAllUsersUnique(usersForConversationCreationCloned)){
            logger.info("Conversation creation error: Users are not unique.");
            return false;
        }

        boolean direct = checkIfDirectConversationBetweenUsers(usersForConversationCreationCloned);

        if(isConversationDuplicated(usersForConversationCreationCloned, conversations)){
            logger.info("Conversation creation error: Conversation with user composition already exists.");
            return false;
        }

        Conversation newConversation =
                new Conversation(
                        generateId(conversations),
                        usersForConversationCreationCloned,
                        direct);

        propagateConversationToUsers(newConversation,newConversation.getUsersInConversation());
        conversations.add(newConversation);
        logger.info("Succesfully added conversation : " + newConversation.getUsersInConversation()
                .stream().map(user -> user.toString()).collect(Collectors.joining(", ")));
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
