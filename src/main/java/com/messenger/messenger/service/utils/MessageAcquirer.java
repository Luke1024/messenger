package com.messenger.messenger.service.utils;

import com.messenger.messenger.model.entity.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class MessageAcquirer {

    public Boolean isStatusChanged(User userRequesting){
        List<ConversationStatus> conversationStatuses =
                userRequesting.getConversations().entrySet().stream()
                        .map(status -> status.getValue()).collect(Collectors.toList());
        for(ConversationStatus status : conversationStatuses){
            if(status.isThereSomethingNew()) return true;
        }
        return false;
    }

    public Map<Conversation, ConversationStatus> getUserConversationStatus(User userRequesting){
        List<ConversationStatus> conversationStatusList =
                userRequesting.getConversations().entrySet().stream()
                .map(entry -> entry.getValue()).collect(Collectors.toList());

        for(ConversationStatus conversationStatus : conversationStatusList){
            conversationStatus.setSomethingChanged(false);
        }
        return userRequesting.getConversations();
    }

    public List<Message> getNewMessages(User userRequesting, long conversationId){
        List<Conversation> userConversations = getUserConversations(userRequesting);
        Optional<Conversation> optionalConversation = findById(conversationId, userConversations);

        if(optionalConversation.isPresent()){
            Conversation conversation = optionalConversation.get();
            ConversationStatus conversationStatus = userRequesting.getConversations().get(conversation);
            List<Message> newMessages = new ArrayList<>();
            newMessages.addAll(conversationStatus.getWaitingMessages());
            clearConversationStatus(conversationStatus);
            return newMessages;
        }
        return new ArrayList<>();
    }

    public Optional<MessageBatchDay> loadLastBatch(User user, long conversationId){
        Optional<Conversation> optionalConversation = findById(conversationId, getUserConversations(user));
        if(optionalConversation.isPresent()){
            return getLastMessageBatch(user, optionalConversation.get());
        } else {
            return Optional.empty();
        }
    }

    public Optional<MessageBatchDay> loadBatch(User userRequesting, long conversationId, int batchId) {
        Optional<Conversation> optionalConversation = findById(conversationId, getUserConversations(userRequesting));
        if (optionalConversation.isPresent()) {
            return getMessageBatch(userRequesting, batchId, optionalConversation.get());
        }
        return Optional.empty();
    }

    private List<Conversation> getUserConversations(User user){
        return user.getConversations().entrySet().stream()
                .map(entry -> entry.getKey()).collect(Collectors.toList());
    }

    private Optional<Conversation> findById(long id, List<Conversation> conversations){
        for(Conversation conversation : conversations){
            if(conversation.getId() == id) return Optional.of(conversation);
        }
        return Optional.empty();
    }

    private Optional<MessageBatchDay> getMessageBatch(User user, int batchIndex, Conversation conversation) {
        ConversationStatus conversationStatus = user.getConversations().get(conversation);
        if(conversationStatus != null) {
            List<MessageBatchDay> messageBatchDays = conversation.getMessageBatchDays();
            if (isBatchIndexInRange(batchIndex, messageBatchDays)) {
                clearConversationStatus(conversationStatus);
                return Optional.of(messageBatchDays.get(batchIndex));
            }
        }
        return Optional.empty();
    }

    private boolean isBatchIndexInRange(int batchIndex, List<MessageBatchDay> messageBatchDays){
        return batchIndex >= 0 && messageBatchDays.size() > batchIndex;
    }

    private Optional<MessageBatchDay> getLastMessageBatch(User user, Conversation conversation){
        ConversationStatus conversationStatus = user.getConversations().get(conversation);
        if(conversationStatus != null){
            clearConversationStatus(conversationStatus);
            conversationStatus.setSomethingChanged(true);
            List<MessageBatchDay> messageBatchDays = conversation.getMessageBatchDays();
            if( ! messageBatchDays.isEmpty()){
                return Optional.of(messageBatchDays.get(messageBatchDays.size()-1));
            }
        }
        return Optional.empty();
    }

    private void clearConversationStatus(ConversationStatus conversationStatus){
        conversationStatus.getWaitingMessages().clear();
        conversationStatus.setNotificationCount(0);
        conversationStatus.setSomethingChanged(false);
    }
}
