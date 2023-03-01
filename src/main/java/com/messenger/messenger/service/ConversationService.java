package com.messenger.messenger.service;

import com.messenger.messenger.model.entity.ConversationStatus;
import com.messenger.messenger.model.entity.User;
import com.messenger.messenger.model.entity.Conversation;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ConversationService {

    private List<Conversation> conversations = new ArrayList<>();

    public boolean addConversation(List<User> usersForConversationCreation, User userCreating){
        usersForConversationCreation.add(userCreating);
        if( ! isConversationWithTheSameUserSquadAlreadyExist(usersForConversationCreation)) {
            Conversation newConversation = new Conversation(generateId(), usersForConversationCreation);
            propagateConversationToUsers(newConversation);
            conversations.add(newConversation);
            return true;
        }
        return false;
    }

    private boolean isConversationWithTheSameUserSquadAlreadyExist(List<User> usersForConversationCreation){
        List<User> clonedUsersForConversationCreation = new ArrayList<>();
        clonedUsersForConversationCreation.addAll(usersForConversationCreation);
        for(Conversation conversation : conversations){
            if(checkIfSquadTheSame(conversation, clonedUsersForConversationCreation)) return true;
        }
        return false;
    }

    private boolean checkIfSquadTheSame(Conversation conversation, List<User> usersForConversationCreation){
        List<User> usersAlreadyInTheConversation = conversation.getUsersInConversation();
        for(User userForConversationCreation : usersForConversationCreation){
            usersAlreadyInTheConversation.remove(userForConversationCreation);
        }
        if(usersAlreadyInTheConversation.isEmpty()) return true;
        else return false;
    }

    private void propagateConversationToUsers(Conversation newConversation){
        newConversation.getUsersInConversation().stream()
                .forEach(user -> user.getConversations().put(newConversation, new ConversationStatus()));
    }

    private long generateId(){
        if(conversations.isEmpty()){
            return 0;
        } else {
            return conversations.get(conversations.size()-1).getId() + 1;
        }
    }

    public Optional<Conversation> findById(long id){
        for(Conversation conversation : conversations){
            if(conversation.getId() == id) return Optional.of(conversation);
        }
        return Optional.empty();
    }
}
