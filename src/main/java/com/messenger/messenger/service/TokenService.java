package com.messenger.messenger.service;


import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class TokenService {

    private SecureRandom random = new SecureRandom();
    int tokenLenght = 16;

    public String generate(){
        int leftLimit = 97;
        int rightLimit = 122;
        int targetStringLength = tokenLenght;
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }
}
