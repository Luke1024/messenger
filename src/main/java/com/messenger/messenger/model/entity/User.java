package com.messenger.messenger.model.entity;

import com.messenger.messenger.model.dto.UserDto;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class User {
    private long id;
    private String name;
    private String password;
    private String identityKey;
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

    public Map<Conversation, ConversationStatus> getConversations() {
        return conversations;
    }

    public UserDto getDto() {
        return new UserDto(this.id, this.name);
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id &&
                name.equals(user.name) &&
                password.equals(user.password) &&
                identityKey.equals(user.identityKey);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", identityKey='" + identityKey + '\'' +
                '}';
    }
}
