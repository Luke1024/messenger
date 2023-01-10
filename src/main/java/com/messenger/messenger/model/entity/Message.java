package com.messenger.messenger.model.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String content;
    private User watchedByUsers;
    private LocalDateTime send;

    public Message(String content, User watchedByUsers, LocalDateTime send) {
        this.content = content;
        this.watchedByUsers = watchedByUsers;
        this.send = send;
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public User getWatchedByUsers() {
        return watchedByUsers;
    }

    public LocalDateTime getSend() {
        return send;
    }
}
