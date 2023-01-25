package com.messenger.messenger.model.entity;

import com.messenger.messenger.service.SettingsService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class Conversation {
    private long id;
    private List<User> usersInConversation;
    private List<MessageBatch> messageBatches = new ArrayList<>();
    private SettingsService settingsService = new SettingsService();

    public Conversation(long id, List<User> users) {
        this.id = id;
        this.usersInConversation = users;
    }

    public long getId() {
        return id;
    }

    public void addWaitingMessage(Message message){
        MessageBatch currentBatch = getCurrentBatch();
        addMessageToBatch(currentBatch,message);
        informUsers(message);
    }

    public List<Message> getOnlyNewMessages(User user){
        ConversationStatus conversationStatus = user.getConversations().get(this);
        if(conversationStatus != null){
            List<Message> newMessages = new ArrayList<>();
            newMessages.addAll(conversationStatus.getWaitingMessages());
            clearConversationStatus(conversationStatus);
            return new ArrayList<>();
        } else {
            return new ArrayList<>();
        }
    }

    public Optional<MessageBatch> getMessageBatch(User user, int batchIndex) {
        ConversationStatus conversationStatus = user.getConversations().get(this);
        if(conversationStatus != null) {
            clearConversationStatus(conversationStatus);
            if(batchIndex >= 0){
                return Optional.of(messageBatches.get(batchIndex));
            } else {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    public List<User> getUsersInConversation() {
        return usersInConversation;
    }

    private void addMessageToBatch(MessageBatch messageBatch , Message message){
        if(messageBatch.getMessages().isEmpty()){
            message.setId(0);
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
            MessageBatch newMessageBatch = new MessageBatch(0, new ArrayList<>());
            messageBatches.add(newMessageBatch);
            return newMessageBatch;
        }
        if( ! messageBatches.isEmpty()){
            MessageBatch lastMessageBatch = messageBatches.get(messageBatches.size()-1);
            if(lastMessageBatch.getMessages().size() > settingsService.messageCountInBatch-1){
                MessageBatch newMessageBatch = new MessageBatch(generateBatchId(), new ArrayList<>());
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
        conversationStatus.clearNotifications();
        conversationStatus.getWaitingMessages().clear();
    }
}
