package com.messenger.messenger.model.dto;

import java.time.LocalDateTime;

public class RequestDto {
    private boolean loadFromSelectedTime;
    private int messagePackedCountToLoad;
    private long openedConversation;
    private LocalDateTime loadFrom;

    public RequestDto() {
    }

    public RequestDto(boolean loadFromSelectedTime, int loadEarlierPacked, long openedConversation, LocalDateTime loadFrom) {
        this.loadFromSelectedTime = loadFromSelectedTime;
        this.messagePackedCountToLoad = loadEarlierPacked;
        this.openedConversation = openedConversation;
        this.loadFrom = loadFrom;
    }

    public boolean isLoadFromSelectedTime() {
        return loadFromSelectedTime;
    }

    public int getMessagePackedCountToLoad() {
        return messagePackedCountToLoad;
    }

    public long getOpenedConversation() {
        return openedConversation;
    }

    public LocalDateTime getLoadFrom() {
        return loadFrom;
    }
}
