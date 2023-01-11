package com.messenger.messenger.model.entity.conversation;

import com.messenger.messenger.model.entity.Message;
import com.messenger.messenger.model.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class Conversation {
    private long id;
    private List<ManagedUser> managedUsers;
    private List<Message> messages = new ArrayList<>();

    private MessageDistributionService distributionService =
            new MessageDistributionService(this, managedUsers, messages);

    public Conversation(long id, List<User> users) {
        this.managedUsers = createManagedUsers(users);
    }

    private List<ManagedUser> createManagedUsers(List<User> users){
        return users.stream().map(user -> new ManagedUser(user)).collect(Collectors.toList());
    }

    public long getId() {
        return id;
    }

    public void addMessage(Message message){
        distributionService.addMessage(message);
    }

    //this is used only when current conversation is open
    public Optional<List<Message>> getOnlyNewMessages(User user){
        return distributionService.getNewMessages(user);
    }

    public Optional<List<Message>> getMessages(User user, int fromIndex, int toIndex) {
        return distributionService.getMessages(user, messageCount);
    }

    public long getMessagesAvailableCount(){
        return messages.size();
    }

    class ManagedUser {
        private User user;
        private List<Message> waitingMessages;
        private int notificationCount;

        public ManagedUser(User user, List<Message> waitingMessages) {
            this.user = user;
            this.waitingMessages = waitingMessages;
        }

        public User getUser() {
            return user;
        }

        public void addNotification(){
            notificationCount++;
        }

        public void addWaitingMessage(Message message){
            waitingMessages.add(message);
        }

        public int getNotificationCount() {
            return notificationCount;
        }

        public void clearWaitingMessages(){
            waitingMessages.clear();
        }

        public void clearNotifications(){
            notificationCount = 0;
        }

        public List<Message> getWaitingMessages() {
            return waitingMessages;
        }
    }
}
