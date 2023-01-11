package com.messenger.messenger.model.entity.conversation;

import com.messenger.messenger.model.entity.Message;
import com.messenger.messenger.model.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MessageDistributionService {

    private Conversation conversation;
    private List<Conversation.ManagedUser> managedUsers;
    private List<Message> messages;

    public MessageDistributionService(Conversation conversation,
                                      List<Conversation.ManagedUser> managedUsers,
                                      List<Message> messages) {
        this.conversation = conversation;
        this.managedUsers = managedUsers;
        this.messages = messages;
    }

    public void addMessage(Message message){
        setId(message);
        messages.add(message);
        informUsers(message);
    }

    private void setId(Message message){
        if(messages.isEmpty()){
            message.setId(0);
        } else {
            long newId = messages.get(messages.size()-1).getId() + 1;
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

    public Optional<List<Message>> getOnlyNewMessages(User user){
        for(Conversation.ManagedUser managedUser : managedUsers){
            if(managedUser.getUser() == user){
                List<Message> waitingMessages = new ArrayList<>();
                waitingMessages.addAll(managedUser.getWaitingMessages());
                managedUser.clearWaitingMessages();
                return Optional.of(waitingMessages);
            }
        }
        return Optional.empty();
    }

    public Optional<List<Message>> getMessages(){

    }
}
