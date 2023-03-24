package com.messenger.messenger.service.utils;

import com.messenger.messenger.model.entity.Conversation;
import com.messenger.messenger.model.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ConversationDuplicationDetector {

    public boolean isConversationWithTheSameUserSquadAlreadyExist(
            List<User> usersForConversationCreation, List<Conversation> conversations){

        for(Conversation conversation : conversations){
            if(checkIfSquadTheSame(conversation, usersForConversationCreation)){
                return true;
            }
        }
        return false;
    }

    private boolean checkIfSquadTheSame(
            Conversation conversation, List<User> usersForConversationCreation){
        List<User> clonedUsersAlreadyInConversation = new ArrayList<>();
        if(conversation.getUsersInConversation().size() < usersForConversationCreation.size()) return false;

        clonedUsersAlreadyInConversation.addAll(conversation.getUsersInConversation());
        for(User userForConversationCreation : usersForConversationCreation){
            clonedUsersAlreadyInConversation.remove(userForConversationCreation);
        }
        if(clonedUsersAlreadyInConversation.isEmpty()){
            return true;
        } else {
            return false;
        }
    }
}
