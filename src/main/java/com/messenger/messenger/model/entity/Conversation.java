package com.messenger.messenger.model.entity;

import com.messenger.messenger.model.dto.MessageDto;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private List<User> users = new ArrayList<>();
    private List<MessageDto> messageDtos = new ArrayList<>();

    public Conversation(List<User> users, List<MessageDto> messageDtos) {
        this.users = users;
        this.messageDtos = messageDtos;
    }

    public long getId() {
        return id;
    }

    public List<User> getUsers() {
        return users;
    }

    public List<MessageDto> getMessageDtos() {
        return messageDtos;
    }
}
