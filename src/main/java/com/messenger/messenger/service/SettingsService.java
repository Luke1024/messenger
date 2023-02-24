package com.messenger.messenger.service;

import org.springframework.stereotype.Service;

@Service
public class SettingsService {
    public String authKey = "AUTH";
    public int messageCountInBatch = 50;
}
