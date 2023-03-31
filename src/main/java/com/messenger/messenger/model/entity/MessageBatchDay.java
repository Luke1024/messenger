package com.messenger.messenger.model.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MessageBatchDay {
    private long id;
    private LocalDate localDate;
    private List<Message> messages = new ArrayList<>();

    public MessageBatchDay(long id, LocalDate localDate) {
        this.id = id;
        this.localDate = localDate;
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
