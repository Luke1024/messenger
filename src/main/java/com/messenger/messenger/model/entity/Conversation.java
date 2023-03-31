package com.messenger.messenger.model.entity;

import java.util.ArrayList;
import java.util.List;

public class Conversation {
    private long id;
    private List<User> usersInConversation;
    private boolean direct;
    private List<MessageBatchDay> messageBatchDays = new ArrayList<>();

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


    public List<MessageBatchDay> getMessageBatchDays() {
        return messageBatchDays;
    }
}
