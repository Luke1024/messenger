package com.messenger.messenger.service;

import com.messenger.messenger.model.dto.*;
import com.messenger.messenger.model.entity.*;
import com.messenger.messenger.service.mapper.MessageMapper;
import com.messenger.messenger.service.utils.ConversationDuplicationDetector;
import com.messenger.messenger.service.utils.ConversationFinder;
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
    private ConversationDuplicationDetector duplicatorDetector;

    @Autowired
    private ConversationFinder conversationFinder;

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
        Optional<Conversation> optionalConversation = findById(conversationId);
        if(optionalConversation.isPresent()){
            Conversation conversation = optionalConversation.get();
            ConversationStatus conversationStatus = userRequesting.getConversations().get(conversation);
            List<Message> newMessages = new ArrayList<>();
            newMessages.addAll(conversationStatus.getWaitingMessages());
            clearConversationStatus(conversationStatus);
            return messageMapper.mapToDtoList(newMessages);
        }
        return new ArrayList<>();
    }

    private void clearConversationStatus(ConversationStatus conversationStatus){
        conversationStatus.clearStatus();
    }

    public Optional<BatchDto> loadLastBatch(User user, long conversationId) {
        Optional<Conversation> optionalConversation = findById(conversationId);
        if(optionalConversation.isPresent()){
            return messageMapper.mapToBatchDtoOptionalFromMessageBatchOptional(
                    getLastMessageBatch(user, optionalConversation.get().getMessageBatches()));
        } else {
            return Optional.empty();
        }
    }

    public Optional<MessageBatch> getLastMessageBatch(User user, List<MessageBatch> messageBatches){
        ConversationStatus conversationStatus = user.getConversations().get(this);
        if(conversationStatus != null){
            clearConversationStatus(conversationStatus);
            if( ! messageBatches.isEmpty()){
                return Optional.of(messageBatches.get(messageBatches.size()-1));
            }
        }
        return Optional.empty();
    }

    public Optional<BatchDto> loadBatch(User userRequesting, long conversationId, long batchId) {
        Optional<Conversation> optionalConversation = findById(conversationId);
        if (optionalConversation.isPresent()) {
            return messageMapper.mapToBatchDtoOptionalFromMessageBatchOptional(
                    getMessageBatch(userRequesting, (int) batchId, optionalConversation.get().getMessageBatches()));
        }
        return Optional.empty();
    }

    public Optional<MessageBatch> getMessageBatch(User user, int batchIndex, List<MessageBatch> messageBatches) {
        ConversationStatus conversationStatus = user.getConversations().get(this);
        if(conversationStatus != null) {
            clearConversationStatus(conversationStatus);
            if (isBatchIndexInRange(batchIndex, messageBatches)) {
                return Optional.of(messageBatches.get(batchIndex));
            }
        }
        return Optional.empty();
    }

    private boolean isBatchIndexInRange(int batchIndex, List<MessageBatch> messageBatches){
        return batchIndex >= 0 && messageBatches.size() > batchIndex;
    }

    private ConversationStatusDto convertToConversationStatusDto(Conversation conversation, ConversationStatus conversationStatus){
        return new ConversationStatusDto(conversation.getId(),
                conversation.getUsersInConversation().stream().map(user -> user.getDto()).collect(Collectors.toList()),
                conversationStatus.getNotificationCount(),
                conversation.isDirect());
    }

    public boolean send(User userRequesting, SendMessageDto sendMessageDto){
        Optional<Conversation> optionalConversation = findById(sendMessageDto.getConversationId());
        if(optionalConversation.isPresent()){
            Message newMessage = new Message(
                    sendMessageDto.getContent(),
                    LocalDateTime.now(),
                    userRequesting,
                    optionalConversation.get());

            addMessage(optionalConversation.get(), newMessage);
            return true;
        } else return false;
    }

    private void addMessage(Conversation conversation, Message newMessage){
        MessageBatch currenctBatch = getCurrentBatch(conversation.getMessageBatches());
        addMessageToBatch(currenctBatch, newMessage);
        informUsers(newMessage, conversation.getUsersInConversation());
    }

    private MessageBatch getCurrentBatch(List<MessageBatch> messageBatches){

        if(messageBatches.isEmpty()){
            MessageBatch newMessageBatch = new MessageBatch(0);
            messageBatches.add(newMessageBatch);
            return newMessageBatch;
        }
        if( ! messageBatches.isEmpty()){
            MessageBatch lastMessageBatch = messageBatches.get(messageBatches.size()-1);
            if(lastMessageBatch.getMessages().size() > settingsService.messageCountInBatch-1){
                MessageBatch newMessageBatch = new MessageBatch(generateBatchId(messageBatches));
                messageBatches.add(newMessageBatch);
                return newMessageBatch;
            }
        }
        return messageBatches.get(messageBatches.size()-1);
    }

    private long generateBatchId(List<MessageBatch> messageBatches){
        return messageBatches.get(messageBatches.size()-1).getId()+1;
    }

    private void informUsers(Message message, List<User> usersInConversation){
        usersInConversation.stream().forEach(user -> addWaitingMessage(user, message));
        usersInConversation.stream().filter(user -> user != message.getByUser())
                .forEach(user -> addNotification(user, message));
    }

    private void addNotification(User user, Message message){
        ConversationStatus conversationStatus = user.getConversations().get(this);
        if(conversationStatus != null) {
            conversationStatus.addNotification();
        }
    }

    private void addWaitingMessage(User user, Message message){
        ConversationStatus conversationStatus = user.getConversations().get(this);
        if(conversationStatus != null){
            conversationStatus.getWaitingMessages().add(message);
        }
    }

    private void addMessageToBatch(MessageBatch messageBatch, Message message){
        if(messageBatch.getMessages().isEmpty()){
            message.setId(0);
            message.setMessageBatch(messageBatch);
        } else {
            List<Message> messages = messageBatch.getMessages();
            long newId = messages.get(messages.size()-1).getId() + 1;
            message.setId(newId);
            message.setMessageBatch(messageBatch);
        }
        messageBatch.getMessages().add(message);
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
