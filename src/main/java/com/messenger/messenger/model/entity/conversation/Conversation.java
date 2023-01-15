package com.messenger.messenger.model.entity.conversation;

import com.messenger.messenger.model.dto.ConversationStatusDto;
import com.messenger.messenger.model.entity.Message;
import com.messenger.messenger.model.entity.MessageBatch;
import com.messenger.messenger.model.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class Conversation {
    private long id;
    private List<ManagedUser> managedUsers;
    private List<MessageBatch> messageBatches = new ArrayList<>();

    private MessageDistributionService distributionService =
            new MessageDistributionService(this, managedUsers, messageBatches);

    public Conversation(long id, List<User> users) {
        this.id = id;
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
    public List<Message> getOnlyNewMessages(User user){
        Optional<ManagedUser> userManagementData = requestUserManagementData(user);
        if(userManagementData.isPresent()) {
            return distributionService.getOnlyNewMessages(user);
        } else {
            return new ArrayList<>();
        }
    }

    public Optional<MessageBatch> getMessageBatch(User user, int batchIndex) {
        Optional<ManagedUser> userManagementData = requestUserManagementData(user);
        if(userManagementData.isPresent()) {
            return distributionService.getMessages(batchIndex);
        } else {
            return Optional.empty();
        }
    }

    public Optional<ConversationStatusDto> getConversationStatus(User user){
        Optional<ManagedUser> managedUser = requestUserManagementData(user);
        if(managedUser.isPresent()){
            return Optional.of(processToConversationDto(managedUser.get()));
        } else {
            return Optional.empty();
        }
    }

    public List<ManagedUser> getManagedUsers(){
        return managedUsers;
    }

    private ConversationStatusDto processToConversationDto(ManagedUser managedUser){
        return new ConversationStatusDto(
                id,
                managedUsers.stream()
                        .filter(user -> user.getUser() != managedUser.getUser())
                        .map(user -> user.getUser().getDto()).collect(Collectors.toList()),
                managedUser.notificationCount);
    }

    private Optional<ManagedUser> requestUserManagementData(User user){
        for(ManagedUser managedUser : managedUsers){
            if(managedUser.getUser() == user) {
                return Optional.of(managedUser);
            }
        }
        return Optional.empty();
    }

    public class ManagedUser {
        private User user;
        private List<Message> waitingMessages = new ArrayList<>();
        private int notificationCount;

        public ManagedUser(User user) {
            this.user = user;
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
