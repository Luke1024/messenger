package com.messenger.messenger.model.dto;

import com.messenger.messenger.model.entity.Message;

import java.time.LocalDateTime;
import java.util.List;

public class RequestDto {
    private boolean loadNew;
    private int messageBatchIndex;
    private long openedConversation;

    public RequestDto() {
    }

    public RequestDto(boolean loadNew, int loadEarlierPacked, long openedConversation) {
        this.loadNew = loadNew;
        this.messageBatchIndex = loadEarlierPacked;
        this.openedConversation = openedConversation;
    }

    public boolean isLoadNew() {
        return loadNew;
    }

    public int getMessageBatchIndex() {
        return messageBatchIndex;
    }

    public long getOpenedConversation() {
        return openedConversation;
    }
}
