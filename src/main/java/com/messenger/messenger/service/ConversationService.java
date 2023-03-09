package com.messenger.messenger.service;

import com.messenger.messenger.model.dto.*;
import com.messenger.messenger.model.entity.*;
import com.messenger.messenger.service.mapper.MessageMapper;
import com.messenger.messenger.service.utils.ConversationDuplicationDetector;
import com.messenger.messenger.service.utils.MessageAcquirer;
import com.messenger.messenger.service.utils.MessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ConversationService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private ConversationDuplicationDetector duplicatorDetector;

    @Autowired
    private MessageAcquirer messageAcquirer;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private SettingsService settingsService;

    private List<Conversation> conversations = new ArrayList<>();

    public Boolean isStatusChanged(User userRequesting){
        List<ConversationStatus> conversationStatuses =
                userRequesting.getConversations().entrySet().stream()
                        .map(status -> status.getValue()).collect(Collectors.toList());
        for(ConversationStatus status : conversationStatuses){
            if(status.isThereSomethingNew()) return true;
        }
        return false;
    }

    public List<ConversationStatusDto> getStatus(User userRequesting){
        return userRequesting.getConversations()
                .entrySet().stream()
                .map(conversationEntry -> convertToConversationStatusDto(conversationEntry.getKey(), conversationEntry.getValue()))
                .collect(Collectors.toList());
    }

    public List<MessageDto> getNewMessages(User userRequesting, long conversationId){
        return messageMapper.mapToDtoList(messageAcquirer.getNewMessages(userRequesting, conversationId));
    }

    public Optional<BatchDto> loadLastBatch(User user, long conversationId) {
        return messageMapper.mapToBatchDtoOptionalFromMessageBatchOptional(
                    messageAcquirer.loadLastBatch(user, conversationId));
    }

    public Optional<BatchDto> loadBatch(User userRequesting, long conversationId, long batchId){
        return messageMapper.mapToBatchDtoOptionalFromMessageBatchOptional(
                messageAcquirer.loadBatch(userRequesting, conversationId, batchId));
    }

    private ConversationStatusDto convertToConversationStatusDto(Conversation conversation, ConversationStatus conversationStatus){
        return new ConversationStatusDto(conversation.getId(),
                conversation.getUsersInConversation().stream().map(user -> user.getDto()).collect(Collectors.toList()),
                conversationStatus.getNotificationCount(),
                conversation.isDirect());
    }

    public boolean send(User userRequesting, SendMessageDto sendMessageDto){
        return messageSender.send(userRequesting, sendMessageDto);
    }

    public boolean addConversation(User userRequesting, List<User> usersForConversationCreation){
        return addConversation(usersForConversationCreation, userRequesting).isPresent();
    }

    public Optional<Long> addConversation(List<User> usersForConversationCreation, User userCreating){
        usersForConversationCreation.add(userCreating);
        boolean directConversationBetweenUsers = false;
        if(usersForConversationCreation.size()==2) directConversationBetweenUsers = true;
        if(usersForConversationCreation.size() != 1){
            if( ! isConversationDuplicated(usersForConversationCreation)) {
                if(usersForConversationCreation.size() != 1) {
                    Conversation newConversation = new Conversation(generateId(), usersForConversationCreation, directConversationBetweenUsers);
                    propagateConversationToUsers(newConversation);
                    conversations.add(newConversation);
                    return Optional.of(newConversation.getId());
                }
            }
        }
        return Optional.empty();
    }

    private boolean isConversationDuplicated(List<User> usersForConversationCreation){
        return duplicatorDetector.isConversationWithTheSameUserSquadAlreadyExist(usersForConversationCreation, conversations);
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
