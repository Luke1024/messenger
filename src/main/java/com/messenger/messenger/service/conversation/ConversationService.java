package com.messenger.messenger.service.conversation;

import com.messenger.messenger.model.dto.UserDto;
import com.messenger.messenger.model.entity.User;
import com.messenger.messenger.model.entity.conversation.Conversation;
import com.messenger.messenger.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ConversationService {

    @Autowired
    private UserService userService;

    private List<Conversation> conversations = new ArrayList<>();

    public boolean addConversation(List<UserDto> userDtoList, User user){
        List<User> usersForConversationCreation = userService.findUsersByDto(userDtoList);
        usersForConversationCreation.add(user);
        conversations.add(new Conversation(generateId(), usersForConversationCreation));
        return true;
    }

    private long generateId(){
        if(conversations.isEmpty()){
            return 0;
        } else {
            return conversations.get(conversations.size()-1).getId() + 1;
        }
    }

    public Optional<Conversation> getConversation(long conversationId){
        List<Conversation> foundConversation = conversations.stream()
                .filter(conversation -> conversation.getId() == conversationId).collect(Collectors.toList());
        if(foundConversation.size()>0){
            return Optional.of(foundConversation.get(0));
        } else {
            return Optional.empty();
        }
    }

    public Optional<Conversation> findById(long id){
        for(Conversation conversation : conversations){
            if(conversation.getId() == id) return Optional.of(conversation);
        }
        return Optional.empty();
    }
}
