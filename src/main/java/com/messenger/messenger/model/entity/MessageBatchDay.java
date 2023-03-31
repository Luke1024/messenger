package com.messenger.messenger.model.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MessageBatchDay {
    private long id;
    private LocalDate localDate = LocalDate.now();
    private List<Message> messages = new ArrayList<>();

    public MessageBatchDay(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }
}
