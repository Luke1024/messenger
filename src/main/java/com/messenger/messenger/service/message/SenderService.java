package com.messenger.messenger.service.message;

import com.messenger.messenger.model.dto.MessageDto;
import com.messenger.messenger.model.entity.conversation.Conversation;
import com.messenger.messenger.model.entity.Message;
import com.messenger.messenger.model.entity.User;
import com.messenger.messenger.repository.ConversationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SenderService {

    @Autowired
    private ConversationRepository conversationRepository;

    public ResponseEntity<Boolean> send(MessageDto messageDto, User user){
        Optional<Conversation> optionalConversation = conversationRepository.findById(messageDto.getConversationId());
        if(optionalConversation.isPresent()){
            optionalConversation.get().addMessage(new Message(
                    messageDto.getContent(),
                    messageDto.getSend(),
                    user,
                    optionalConversation.get())
            );
        }
    }
}
