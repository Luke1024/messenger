package com.messenger.messenger.service;

import com.messenger.messenger.model.dto.*;
import com.messenger.messenger.model.entity.*;
import com.messenger.messenger.service.mapper.ConversationMapper;
import com.messenger.messenger.service.mapper.MessageMapper;
import com.messenger.messenger.service.utils.ConversationAdder;
import com.messenger.messenger.service.utils.MessageAcquirer;
import com.messenger.messenger.service.utils.MessageSender;
import com.messenger.messenger.service.utils.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ConversationService {

    @Autowired
    private MessageAcquirer messageAcquirer;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private Settings settings;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private ConversationMapper conversationMapper;

    @Autowired
    private ConversationAdder conversationAdder;

    public List<Conversation> conversations = new ArrayList<>();

    public Boolean isStatusChanged(User userRequesting){
        return messageAcquirer.isStatusChanged(userRequesting);
    }

    public List<ConversationStatusDto> getStatus(User userRequesting){
        return conversationMapper.mapToConversationStatusDto(userRequesting,messageAcquirer.getUserConversationStatus(userRequesting));
    }

    public List<MessageDto> getNewMessages(User userRequesting, long conversationId){
        return messageMapper.mapToDtoList(messageAcquirer.getNewMessages(userRequesting, conversationId), userRequesting);
    }

    public Optional<BatchDto> loadLastBatch(User userRequesting, long conversationId) {
        return messageMapper.mapToBatchDtoOptionalFromMessageBatchOptional(
                    messageAcquirer.loadLastBatch(userRequesting, conversationId), userRequesting);
    }

    public Optional<BatchDto> loadBatch(User userRequesting, long conversationId, int batchId){
        return messageMapper.mapToBatchDtoOptionalFromMessageBatchOptional(
                messageAcquirer.loadBatch(userRequesting, conversationId, batchId), userRequesting);
    }

    public boolean send(User userRequesting, SendMessageDto sendMessageDto){
        return messageSender.send(userRequesting, sendMessageDto);
    }

    public boolean addConversation(User userCreating, List<User> usersForConversationCreation){
        return conversationAdder.addConversation(userCreating, usersForConversationCreation, conversations);
    }

    public Optional<Conversation> findById(long id){
        for(Conversation conversation : conversations){
            if(conversation.getId() == id) return Optional.of(conversation);
        }
        return Optional.empty();
    }
}
