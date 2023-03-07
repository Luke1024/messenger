package com.messenger.messenger.model.entity;

import com.messenger.messenger.service.SettingsService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class Conversation {
    private long id;
    private List<User> usersInConversation;
    private boolean direct;
    private List<MessageBatch> messageBatches = new ArrayList<>();
    private SettingsService settingsService = new SettingsService();

    public Conversation(long id, List<User> users, boolean direct) {
        this.id = id;
        this.usersInConversation = users;
        this.direct = direct;
    }

    public long getId() {
        return id;
    }

    public void addWaitingMessage(Message message){
        MessageBatch currentBatch = getCurrentBatch();
        addMessageToBatch(currentBatch,message);
        informUsers(message);
    }

    public boolean isDirect() {
        return direct;
    }

    public List<Message> getOnlyNewMessages(User user){
        ConversationStatus conversationStatus = user.getConversations().get(this);
        if(conversationStatus != null){
            List<Message> newMessages = new ArrayList<>();
            newMessages.addAll(conversationStatus.getWaitingMessages());
            clearConversationStatus(conversationStatus);
            return newMessages;
        } else {
            return new ArrayList<>();
        }
    }

    public Optional<MessageBatch> getLastMessageBatch(User user){
        ConversationStatus conversationStatus = user.getConversations().get(this);
        if(conversationStatus != null){
            clearConversationStatus(conversationStatus);
            if( ! messageBatches.isEmpty()){
                return Optional.of(messageBatches.get(messageBatches.size()-1));
            }
        }
        return Optional.empty();
    }

    public Optional<MessageBatch> getMessageBatch(User user, int batchIndex) {
        ConversationStatus conversationStatus = user.getConversations().get(this);
        if(conversationStatus != null) {
            clearConversationStatus(conversationStatus);
            if (isBatchIndexInRange(batchIndex)) {
                return Optional.of(messageBatches.get(batchIndex));
            }
        }
        return Optional.empty();
    }

    private boolean isBatchIndexInRange(int batchIndex){
        return batchIndex >= 0 && messageBatches.size() > batchIndex;
    }

    public List<User> getUsersInConversation() {
        return usersInConversation;
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

    private MessageBatch getCurrentBatch(){
        if(messageBatches.isEmpty()){
            MessageBatch newMessageBatch = new MessageBatch(0);
            messageBatches.add(newMessageBatch);
            return newMessageBatch;
        }
        if( ! messageBatches.isEmpty()){
            MessageBatch lastMessageBatch = messageBatches.get(messageBatches.size()-1);
            if(lastMessageBatch.getMessages().size() > settingsService.messageCountInBatch-1){
                MessageBatch newMessageBatch = new MessageBatch(generateBatchId());
                messageBatches.add(newMessageBatch);
                return newMessageBatch;
            }
        }
        return messageBatches.get(messageBatches.size()-1);
    }

    private long generateBatchId(){
        return messageBatches.get(messageBatches.size()-1).getId()+1;
    }

    private void informUsers(Message message){
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

    private void clearConversationStatus(ConversationStatus conversationStatus){
        conversationStatus.clearStatus();
    }
}
