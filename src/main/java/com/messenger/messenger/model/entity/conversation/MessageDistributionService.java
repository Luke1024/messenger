package com.messenger.messenger.model.entity.conversation;

import com.messenger.messenger.model.entity.Message;
import com.messenger.messenger.model.entity.MessageBatch;
import com.messenger.messenger.model.entity.User;
import com.messenger.messenger.service.SettingsService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MessageDistributionService {

    private Conversation conversation;
    private List<Conversation.ManagedUser> managedUsers;
    private List<MessageBatch> messageBatches;

    private SettingsService settingsService = new SettingsService();

    public MessageDistributionService(Conversation conversation,
                                      List<Conversation.ManagedUser> managedUsers,
                                      List<MessageBatch> messageBatches) {
        this.conversation = conversation;
        this.managedUsers = managedUsers;
        this.messageBatches = messageBatches;
    }

    public void addMessage(Message message){
        MessageBatch currentBatch = getCurrentBatch();

        configureMessage(currentBatch,message);
        currentBatch.getMessages().add(message);
        informUsers(message);
    }

    private MessageBatch getCurrentBatch(){
        if(messageBatches.isEmpty()){
            MessageBatch newMessageBatch = new MessageBatch(0, new ArrayList<>());
            return newMessageBatch;
        }
        if( ! messageBatches.isEmpty()){
            MessageBatch lastMessageBatch = messageBatches.get(messageBatches.size()-1);
            if(lastMessageBatch.getMessages().size() > settingsService.messageCountInBatch-1){
                return new MessageBatch(generateBatchId(), new ArrayList<>());
            }
        }
        return messageBatches.get(messageBatches.size()-1);
    }

    private long generateBatchId(){
        return messageBatches.get(messageBatches.size()-1).getId()+1;
    }

    private void configureMessage(MessageBatch messageBatch ,Message message){
        if(messageBatch.getMessages().isEmpty()){
            message.setId(0);
        } else {
            List<Message> messages = messageBatch.getMessages();
            long newId = messages.get(messages.size()-1).getId() + 1;
            message.setId(newId);
            message.setMessageBatch(messageBatch);
        }
    }

    private void informUsers(Message message){
        managedUsers.stream().filter(managedUser -> managedUser.getUser() != message.getByUser())
                .forEach(managedUser -> informUser(managedUser, message));
    }

    private void informUser(Conversation.ManagedUser managedUser, Message message){
        managedUser.addNotification();
        managedUser.addWaitingMessage(message);
    }

    public List<Message> getOnlyNewMessages(User user){
        for(Conversation.ManagedUser managedUser : managedUsers){
            if(managedUser.getUser() == user){
                List<Message> waitingMessages = new ArrayList<>();
                waitingMessages.addAll(managedUser.getWaitingMessages());
                managedUser.clearWaitingMessages();
                return waitingMessages;
            }
        }
        return new ArrayList<>();
    }

    public int getCurrentBatchIndex(){
        return messageBatches.size()-1;
    }

    public Optional<MessageBatch> getMessages(int batchIndex){
        if(batchIndex >= 0){
            return Optional.of(messageBatches.get(batchIndex));
        } else {
            return Optional.empty();
        }
    }
}
