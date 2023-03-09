package com.messenger.messenger.service.utils;

import com.messenger.messenger.model.entity.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class MessageAcquirer {


    public List<Message> getNewMessages(User userRequesting, long conversationId, List<Conversation> conversations){
        Optional<Conversation> optionalConversation = findById(conversationId, conversations);
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

    private Optional<Conversation> findById(long id, List<Conversation> conversations){
        for(Conversation conversation : conversations){
            if(conversation.getId() == id) return Optional.of(conversation);
        }
        return Optional.empty();
    }

    public Optional<MessageBatch> loadLastBatch(User user, long conversationId, List<Conversation> conversations){
        Optional<Conversation> optionalConversation = findById(conversationId, conversations);
        if(optionalConversation.isPresent()){
            return getLastMessageBatch(user, optionalConversation.get().getMessageBatches());
        } else {
            return Optional.empty();
        }
    }

    public Optional<MessageBatch> loadBatch(User userRequesting, long conversationId, long batchId, List<Conversation> conversations) {
        Optional<Conversation> optionalConversation = findById(conversationId, conversations);
        if (optionalConversation.isPresent()) {
            return getMessageBatch(userRequesting, (int) batchId, optionalConversation.get().getMessageBatches());
        }
        return Optional.empty();
    }

    private Optional<MessageBatch> getMessageBatch(User user, int batchIndex, List<MessageBatch> messageBatches) {
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

    private Optional<MessageBatch> getLastMessageBatch(User user, List<MessageBatch> messageBatches){
        ConversationStatus conversationStatus = user.getConversations().get(this);
        if(conversationStatus != null){
            clearConversationStatus(conversationStatus);
            if( ! messageBatches.isEmpty()){
                return Optional.of(messageBatches.get(messageBatches.size()-1));
            }
        }
        return Optional.empty();
    }

    private void clearConversationStatus(ConversationStatus conversationStatus){
        conversationStatus.clearStatus();
    }
}
