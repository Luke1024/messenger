package com.messenger.messenger.model.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private List<User> users;
    private List<Message> messages = new ArrayList<>();

    public Conversation(List<User> users) {
        this.users = users;
    }

    public long getId() {
        return id;
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Message> getMessages() {
        return messages;
    }
}
