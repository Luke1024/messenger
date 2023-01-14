package com.messenger.messenger.service.conversation;

import com.messenger.messenger.model.dto.UserDto;
import com.messenger.messenger.model.entity.User;
import com.messenger.messenger.model.entity.conversation.Conversation;
import com.messenger.messenger.repository.ConversationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ConversationService {

    private List<Conversation> conversations = new ArrayList<>();

    public String addConversation(List<UserDto> userDtoList, User user){
        List<Conversation> userConversations = user.getConversations();
        Optional<Conversation> theSameConversationOptional = findTheSameConversation(userDtoList);
        if(theSameConversationOptional.isPresent()){
            return "Conversation already exists";
        } else {
            conversationRepository.add(new Conversation(
                    generateId(),
                    getUsersFromDtos(userDtoList)
            ));
            return "Conversation created";
        }
    }

    private long generateId(){
        Optional<Conversation> conversation = conversationRepository.getLastConversation();
        if(conversation.isPresent()){

        }
    }

    private List<User> getUsersFromDtos(List<UserDto> userDtos){

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
}
