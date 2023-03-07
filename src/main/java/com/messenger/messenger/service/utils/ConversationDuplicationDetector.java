package com.messenger.messenger.service.utils;

import com.messenger.messenger.model.entity.Conversation;
import com.messenger.messenger.model.entity.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ConversationDuplicationDetector {

    public boolean isConversationWithTheSameUserSquadAlreadyExist(
            List<User> usersForConversationCreation, List<Conversation> conversations){
        List<User> clonedUsersForConversationCreation = new ArrayList<>();
        clonedUsersForConversationCreation.addAll(usersForConversationCreation);
        for(Conversation conversation : conversations){
            if(checkIfSquadTheSame(conversation, clonedUsersForConversationCreation)){
                return true;
            }
        }
        return false;
    }

    private boolean checkIfSquadTheSame(
            Conversation conversation, List<User> usersForConversationCreation){
        List<User> usersAlreadyInTheConversation = conversation.getUsersInConversation();
        for(User userInConversation : usersAlreadyInTheConversation){
            usersForConversationCreation.remove(userInConversation);
        }
        if(usersForConversationCreation.isEmpty()){
            return true;
        } else {
            return false;
        }
    }
}
