package com.messenger.messenger.model.entity;

import com.messenger.messenger.model.dto.ConversationStatusDto;
import com.messenger.messenger.model.dto.UserDto;
import com.messenger.messenger.model.entity.conversation.Conversation;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

public class User {
    private long id;
    private String name;
    private String password;
    private String identityKey;
    private List<User> usersSaved = new ArrayList<>();
    private List<Conversation> conversations = new ArrayList<>();
    private List<ConversationStatusDto> conversationStatuses = new ArrayList<>();

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

    public List<Conversation> getConversations() {
        return conversations;
    }

    public List<ConversationStatusDto> getConversationStatuses() {
        return conversationStatuses;
    }

    public UserDto getDto() {
        return new UserDto(this.id, this.name);
    }

    public void setId(long id) {
        this.id = id;
    }
}
