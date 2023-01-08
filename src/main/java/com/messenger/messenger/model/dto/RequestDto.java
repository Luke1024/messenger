package com.messenger.messenger.model.dto;

import java.time.LocalDateTime;

public class RequestDto {
    private boolean loadNew;
    private int loadEarlierPacked;
    private long openedConversation;
    private LocalDateTime loadFrom;

    public RequestDto() {
    }

    public RequestDto(boolean loadNew, int loadEarlierPacked, long openedConversation, LocalDateTime loadFrom) {
        this.loadNew = loadNew;
        this.loadEarlierPacked = loadEarlierPacked;
        this.openedConversation = openedConversation;
        this.loadFrom = loadFrom;
    }

    public boolean isLoadNew() {
        return loadNew;
    }

    public int getLoadEarlierPacked() {
        return loadEarlierPacked;
    }

    public long getOpenedConversation() {
        return openedConversation;
    }

    public LocalDateTime getLoadFrom() {
        return loadFrom;
    }
}