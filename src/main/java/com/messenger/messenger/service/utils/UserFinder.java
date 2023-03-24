package com.messenger.messenger.service.utils;

import com.messenger.messenger.model.dto.UserDto;
import com.messenger.messenger.model.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class UserFinder {

    @Autowired
    private Settings settings;

    private Logger logger = LoggerFactory.getLogger(UserFinder.class);

    public Optional<User> findUserByHttpRequest(HttpServletRequest request, List<User> users){
        Optional<String> optionalIdentityKey = getIdentityKey(request);
        if(optionalIdentityKey.isPresent()) {
            return findByIdentityKey(optionalIdentityKey.get(), users);
        } else {
            return Optional.empty();
        }
    }

    public Optional<User> findByIdentityKey(String identityKey, List<User> users){
        for(User user : users){
            if(user.getIdentityKey().equals(identityKey)) return Optional.of(user);
        }
        return Optional.empty();
    }

    public List<User> findUsersByDto(List<UserDto> userDtos, List<User> users){
        List<User> usersFound = new ArrayList<>();
        for(UserDto userDto : userDtos){
            Optional<User> optionalUser = findById(userDto.getUserId(), users);
            if(optionalUser.isPresent()){
                if(optionalUser.get().getName().equals(userDto.getUserName())){
                    usersFound.add(optionalUser.get());
                }
            }
        }
        return usersFound;
    }

    public List<UserDto> findUsersByNameToDto(String userName, List<User> users){
        List<User> usersFound = findUsersByStringContainedInTheName(userName, users);
        return usersFound.stream().map(user -> user.getDto()).collect(Collectors.toList());
    }

    private Optional<String> getIdentityKey(HttpServletRequest request){
        if(request.getCookies() != null) {
            Cookie[] cookies = request.getCookies();
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(settings.authKey)) {
                    return Optional.of(cookie.getValue());
                }
            }
        }
        return Optional.empty();
    }

    public Optional<User> findByName(String userName, List<User> users){
        for(User user : users){
            if(user.getName().equals(userName)) return Optional.of(user);
        }
        return Optional.empty();
    }

    public Optional<User> findById(long userId, List<User> users){
        for(User user : users){
            if(user.getId() == userId) return Optional.of(user);
        }
        return Optional.empty();
    }

    private List<User> findUsersByStringContainedInTheName(
            String userName, List<User> users){

        List<User> usersCompatible = new ArrayList<>();
        for(User user : users){
            if(user.getName().toLowerCase().contains(userName.toLowerCase())) {
                usersCompatible.add(user);
            }
        }
        return usersCompatible;
    }
}
