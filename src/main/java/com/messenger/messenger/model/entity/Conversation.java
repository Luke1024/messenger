package com.messenger.messenger.model.entity;

import com.messenger.messenger.service.SettingsService;

import java.util.ArrayList;
import java.util.List;

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

    public boolean isDirect() {
        return direct;
    }

    public List<User> getUsersInConversation() {
        return usersInConversation;
    }


    public List<MessageBatch> getMessageBatches() {
        return messageBatches;
    }
}
