package com.messenger.messenger.service.utils;

import org.springframework.stereotype.Component;

@Component
public class Settings {
    public String authKey = "AUTH";
    public int messageCountInBatch = 50;
}
