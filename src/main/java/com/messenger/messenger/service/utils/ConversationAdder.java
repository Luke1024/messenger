package com.messenger.messenger.service.utils;

import com.messenger.messenger.model.dto.AddConversationResponse;
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

@Component
public class ConversationAdder {

    @Autowired
    private ConversationDuplicationDetector duplicatorDetector;
    private String errorGeneral = "Conversation creation error. ";
    private String noUsers = "There is no users for conversation creation.";
    private String notUniqueUsers = "Users are not unique.";
    private String userExists = "User already added.";
    private String compositionAlreadyExists = "Conversation with user composition already exists.";

    public AddConversationResponse addConversation(
            User userCreating,
            List<User> usersForConversationCreation,
            List<Conversation> conversations){

        List<User> usersForConversationCreationCloned = new ArrayList<>();
        usersForConversationCreationCloned.addAll(usersForConversationCreation);

        if(usersForConversationCreationCloned.isEmpty()){
            String response = errorGeneral + noUsers;
            return new AddConversationResponse(false, response);
        }

        usersForConversationCreationCloned.add(userCreating);
        if( ! isAllUsersUnique(usersForConversationCreationCloned)){
            String response = errorGeneral + notUniqueUsers;
            return new AddConversationResponse(false, response);
        }

        boolean direct = checkIfDirectConversationBetweenUsers(usersForConversationCreationCloned);

        if(isConversationDuplicated(usersForConversationCreationCloned, conversations)){
            String response;
            if(usersForConversationCreation.size()==1){
                response = errorGeneral + userExists;
            } else {
                response = errorGeneral + compositionAlreadyExists;
            }
            return new AddConversationResponse(false, response);
        }

        Conversation newConversation =
                new Conversation(
                        generateId(conversations),
                        usersForConversationCreationCloned,
                        direct);

        propagateConversationToUsers(newConversation,newConversation.getUsersInConversation());
        conversations.add(newConversation);
        return new AddConversationResponse(true, "");
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
