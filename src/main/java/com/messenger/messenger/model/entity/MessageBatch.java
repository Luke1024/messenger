package com.messenger.messenger.model.entity;

import java.util.ArrayList;
import java.util.List;

public class MessageBatch {
    private long id;
    private List<Message> messages = new ArrayList<>();

    public MessageBatch(long id, List<Message> messages) {
        this.id = id;
        this.messages = messages;
    }

    public long getId() {
        return id;
    }

    public List<Message> getMessages() {
        return messages;
    }
}
