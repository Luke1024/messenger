package com.messenger.messenger.model.entity;


import com.messenger.messenger.model.entity.conversation.Conversation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Message {
    private long id;
    private String content;
    private LocalDateTime send;
    private List<User> watchedByUsers = new ArrayList<>();
    private User byUser;
    private Conversation conversation;
    private MessageBatch messageBatch;

    public Message(String content, LocalDateTime send, User userBy, Conversation conversation) {
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

    public LocalDateTime getSend() {
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

    public MessageBatch getMessageBatch() {
        return messageBatch;
    }

    public void setMessageBatch(MessageBatch messageBatch) {
        this.messageBatch = messageBatch;
    }
}
