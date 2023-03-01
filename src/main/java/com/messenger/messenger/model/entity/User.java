package com.messenger.messenger.model.entity;

import com.messenger.messenger.model.dto.UserDto;

import java.util.HashMap;
import java.util.Map;

public class User {
    private long id;
    private String name;
    private String password;
    private String identityKey;
    private Map<Long, User> usersWithKeyAsDefaultConversation = new HashMap<>();
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

    public Map<Long, User> getUsersWithKeyAsDefaultConversation() {
        return usersWithKeyAsDefaultConversation;
    }

    public Map<Conversation, ConversationStatus> getConversations() {
        return conversations;
    }

    public UserDto getDto() {
        return new UserDto(this.id, this.name, -1);
    }

    public UserDto getDtoWithDefaultConversation(User userRequesting){
        for(Map.Entry<Long, User> entry : usersWithKeyAsDefaultConversation.entrySet()){
            if(entry.getValue()==userRequesting){
                return new UserDto(this.id, this.name, entry.getKey());
            }
        }
        return getDto();
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isConversationStatusChanged(){
        for(ConversationStatus conversationStatus : conversations.values()){
            if(conversationStatus.isThereSomethingNew()) return true;
        }
        return false;
    }
}
