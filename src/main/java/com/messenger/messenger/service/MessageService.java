package com.messenger.messenger.service;

import com.messenger.messenger.model.dto.*;
import com.messenger.messenger.model.entity.*;
import com.messenger.messenger.service.mapper.MessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MessageService {

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private UserService userService;

    public Boolean isStatusChanged(User userRequesting){
        return userRequesting.isConversationStatusChanged();
    }

    public List<ConversationStatusDto> getConversationStatus(User userRequesting){
        return userRequesting.getConversations()
                .entrySet().stream()
                .map(conversationEntry -> convertToConversationStatusDto(conversationEntry.getKey(), conversationEntry.getValue()))
                .collect(Collectors.toList());
    }

    public List<MessageDto> getNewMessages(User userRequesting, long conversationId){
        Optional<Conversation> optionalConversation = conversationService.findById(conversationId);
        if(optionalConversation.isPresent()){
            return messageMapper.mapToDtoList(optionalConversation.get().getOnlyNewMessages(userRequesting));
        }
        return new ArrayList<>();
    }

    public Optional<BatchDto> load(User userRequesting, long conversationId, long batchId) {
        Optional<Conversation> optionalConversation = conversationService.findById(conversationId);
        if (optionalConversation.isPresent()) {
            return messageMapper.mapToBatchDtoOptionalFromMessageBatchOptional(
                    optionalConversation.get().getMessageBatch(userRequesting, (int) batchId));
        }
        return Optional.empty();
    }

    private ConversationStatusDto convertToConversationStatusDto(Conversation conversation, ConversationStatus conversationStatus){
        return new ConversationStatusDto(conversation.getId(),
                conversation.getUsersInConversation().stream().map(user -> user.getDto()).collect(Collectors.toList()),
                conversationStatus.getNotificationCount());
    }

    public boolean send(User userRequesting, SendMessageDto sendMessageDto){
        Optional<Conversation> optionalConversation = conversationService.findById(sendMessageDto.getConversationId());
        if(optionalConversation.isPresent()){
            optionalConversation.get().addWaitingMessage(new Message(
                    sendMessageDto.getContent(),
                    LocalDateTime.now(),
                    userRequesting,
                    optionalConversation.get())
            );
            return true;
        } else return false;
    }

    public boolean addConversation(User userRequesting, List<UserDto> userDtos){
        return conversationService.addConversation(userDtos, userRequesting);
    }
}
