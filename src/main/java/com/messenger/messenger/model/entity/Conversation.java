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
}
