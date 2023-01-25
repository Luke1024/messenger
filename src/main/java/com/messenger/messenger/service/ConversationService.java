package com.messenger.messenger.service;

import com.messenger.messenger.model.dto.UserDto;
import com.messenger.messenger.model.entity.ConversationStatus;
import com.messenger.messenger.model.entity.User;
import com.messenger.messenger.model.entity.Conversation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ConversationService {

    @Autowired
    private UserService userService;

    private List<Conversation> conversations = new ArrayList<>();

    public boolean addConversation(List<UserDto> userDtoList, User user){
        List<User> usersForConversationCreation = userService.findUsersByDto(userDtoList);
        usersForConversationCreation.add(user);
        Conversation newConversation = new Conversation(generateId(), usersForConversationCreation);
        propagateConversationToUsers(newConversation);
        conversations.add(newConversation);
        return true;
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