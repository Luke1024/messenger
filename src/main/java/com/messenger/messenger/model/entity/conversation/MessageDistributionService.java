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
        setId(message);
        messageBatches.add(message);
        informUsers(message);
    }

    private void setId(Message message){
        if(messageBatches.isEmpty()){
            message.setId(0);
        } else {
            long newId = messageBatches.get(messageBatches.size()-1).getId() + 1;
            message.setId(newId);
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

    public List<Message> getMessages(User user, int batchIndex){
        return ;
    }
}
