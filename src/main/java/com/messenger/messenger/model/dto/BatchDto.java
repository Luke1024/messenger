package com.messenger.messenger.model.dto;

import java.util.List;

public class BatchDto {
    private long id;
    private String send;
    private List<MessageDto> messageDtoList;

    public BatchDto() {
    }

    public BatchDto(long id, String send, List<MessageDto> messageDtoList) {
        this.id = id;
        this.send = send;
        this.messageDtoList = messageDtoList;
    }

    public long getId() {
        return id;
    }

    public String getSend() {
        return send;
    }

    public List<MessageDto> getMessageDtoList() {
        return messageDtoList;
    }
}
