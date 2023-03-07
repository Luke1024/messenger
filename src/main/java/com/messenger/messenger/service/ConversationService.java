package com.messenger.messenger.service;

import com.messenger.messenger.model.dto.*;
import com.messenger.messenger.model.entity.*;
import com.messenger.messenger.service.mapper.MessageMapper;
import com.messenger.messenger.service.utils.ConversationDuplicationDetector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ConversationService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private ConversationDuplicationDetector duplicatorDetector;

    private List<Conversation> conversations = new ArrayList<>();

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
        Optional<Conversation> optionalConversation = findById(conversationId);
        if(optionalConversation.isPresent()){
            return messageMapper.mapToDtoList(optionalConversation.get().getOnlyNewMessages(userRequesting));
        }
        return new ArrayList<>();
    }

    public Optional<BatchDto> loadLastBatch(User user, long conversationId) {
        Optional<Conversation> optionalConversation = findById(conversationId);
        if(optionalConversation.isPresent()){
            return messageMapper.mapToBatchDtoOptionalFromMessageBatchOptional
                    (optionalConversation.get().getLastMessageBatch(user));
        } else {
            return Optional.empty();
        }
    }

    public Optional<BatchDto> loadBatch(User userRequesting, long conversationId, long batchId) {
        Optional<Conversation> optionalConversation = findById(conversationId);
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
        Optional<Conversation> optionalConversation = findById(sendMessageDto.getConversationId());
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

    public boolean addConversation(User userRequesting, List<User> usersForConversationCreation){
        return addConversation(usersForConversationCreation, userRequesting).isPresent();
    }

    public Optional<Long> addConversation(List<User> usersForConversationCreation, User userCreating){
        usersForConversationCreation.add(userCreating);
        if( ! duplicatorDetector.isConversationWithTheSameUserSquadAlreadyExist(usersForConversationCreation, conversations)) {
            Conversation newConversation = new Conversation(generateId(), usersForConversationCreation);
            propagateConversationToUsers(newConversation);
            conversations.add(newConversation);
            return Optional.of(newConversation.getId());
        }
        return Optional.empty();
    }

    private void propagateConversationToUsers(Conversation newConversation){
        newConversation.getUsersInConversation().stream()
                .forEach(user -> user.getConversations().put(newConversation, new ConversationStatus()));
    }

    private long generateId(){
        if(conversations.isEmpty()) return 0;
        else return conversations.get(conversations.size()-1).getId() + 1;
    }

    public Optional<Conversation> findById(long id){
        for(Conversation conversation : conversations){
            if(conversation.getId() == id) return Optional.of(conversation);
        }
        return Optional.empty();
    }
}
