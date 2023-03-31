package com.messenger.messenger.model.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MessageBatchDay {
    private long id;
    private LocalDate send;
    private List<Message> messages = new ArrayList<>();

    public MessageBatchDay(long id, LocalDate send) {
        this.id = id;
        this.send = send;
    }

    public long getId() {
        return id;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public LocalDate getSend() {
        return send;
    }
}
