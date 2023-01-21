package com.messenger.messenger.model.entity;

import com.messenger.messenger.model.dto.UserDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    private long id;
    private String name;
    private String password;
    private String identityKey;
    private List<User> usersSaved = new ArrayList<>();
    private Map<Conversation, ConversationStatus> conversations = new HashMap<>();


    public User(String name, String password, String identityKey) {
        this.name = name;
        this.password = password;
        this.identityKey = identityKey;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getIdentityKey() {
        return identityKey;
    }

    public List<User> getUsersSaved() {
        return usersSaved;
    }

    public Map<Conversation, ConversationStatus> getConversations() {
        return conversations;
    }

    public UserDto getDto() {
        return new UserDto(this.id, this.name);
    }

    public void setId(long id) {
        this.id = id;
    }
}
