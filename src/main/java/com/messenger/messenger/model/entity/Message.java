package com.messenger.messenger.model.entity;


import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Message {
    private long id;
    private String content;
    private LocalTime send;
    private List<User> watchedByUsers = new ArrayList<>();
    private User byUser;
    private Conversation conversation;
    private MessageBatchDay messageBatchDay;

    public Message(String content, LocalTime send, User userBy, Conversation conversation) {
        this.content = content;
        this.send = send;
        this.byUser = userBy;
        this.conversation = conversation;
    }

    public void setId(long id){
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public LocalTime getSend() {
        return send;
    }

    public void addUserSeen(User user){
        watchedByUsers.add(user);
    }

    public List<User> getWatchedByUsers() {
        return watchedByUsers;
    }

    public User getByUser() {
        return byUser;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public MessageBatchDay getMessageBatchDay() {
        return messageBatchDay;
    }

    public void setMessageBatchDay(MessageBatchDay messageBatchDay) {
        this.messageBatchDay = messageBatchDay;
    }
}
