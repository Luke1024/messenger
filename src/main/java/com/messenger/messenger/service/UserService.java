package com.messenger.messenger.service;

import com.messenger.messenger.model.dto.UserDataDto;
import com.messenger.messenger.model.dto.UserDto;
import com.messenger.messenger.model.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private SettingsService settingsService;

    private Logger logger = LoggerFactory.getLogger(UserService.class);

    private List<User> users = new ArrayList<>();

    public Optional<User> findUserByHttpRequest(HttpServletRequest request){
        Optional<String> optionalIdentityKey = getIdentityKey(request);
        if(optionalIdentityKey.isPresent()) {
            logger.info("User is pinging with authKey: " + optionalIdentityKey.get());
            return findByIdentityKey(optionalIdentityKey.get());
        } else {
            return Optional.empty();
        }
    }

    public boolean register(UserDataDto userDataDto){
        if(isRegistrationAllowed(userDataDto)){
            logger.info("Registering user with name: " + userDataDto.getUserName()
                    + " and password: " + userDataDto.getPassword());
            User newUser = generateNewUserFromDataDto(userDataDto);
            newUser.setId(generateId());
            users.add(newUser);
            return true;
        } else return false;
    }

    public boolean loginUser(UserDataDto userDataDto, HttpServletResponse response){
        Optional<User> userOptional = findByName(userDataDto.getUserName());
        if(userOptional.isPresent()) {
            if (isPasswordValid(userOptional.get().getPassword(),userDataDto.getPassword())) {
                addIdentityCookie(userOptional.get().getIdentityKey(),response);
                return true;
            }
        }
        return false;
    }

    public List<User> findUsersByDto(List<UserDto> userDtos){
        List<User> users = new ArrayList<>();
        for(UserDto userDto : userDtos){
            Optional<User> optionalUser = findById(userDto.getUserId());
            if(optionalUser.isPresent()){
                if(optionalUser.get().getName().equals(userDto.getUserName())){
                    users.add(optionalUser.get());
                }
            }
        }
        return users;
    }

    public List<UserDto> findUsers(String userName){
        List<User> users = findUsersByStringContainedInTheName(userName);
        return users.stream().map(user -> user.getDto()).collect(Collectors.toList());
    }

    public Optional<User> findByName(String userName){
        for(User user : users){
            if(user.getName().equals(userName)) return Optional.of(user);
        }
        return Optional.empty();
    }

    public Optional<User> findByIdentityKey(String identityKey){
        for(User user : users){
            if(user.getIdentityKey().equals(identityKey)) return Optional.of(user);
        }
        return Optional.empty();
    }

    private Optional<String> getIdentityKey(HttpServletRequest request){
        if(request.getCookies() != null) {
            Cookie[] cookies = request.getCookies();
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(settingsService.authKey)) {
                    return Optional.of(cookie.getValue());
                }
            }
        }
        return Optional.empty();
    }

    private boolean isRegistrationAllowed(UserDataDto userDataDto){
        Optional<User> userOptional = findByName(userDataDto.getUserName());
        if(userOptional.isPresent()){
            return false;
        } else return true;
    }

    private User generateNewUserFromDataDto(UserDataDto userDataDto){
        return new User(userDataDto.getUserName(), userDataDto.getPassword(), generateIdentityKey());
    }

    private String generateIdentityKey(){
        return tokenService.generate();
    }

    private boolean isPasswordValid(String passwordReal, String passwordGiven){
        return passwordReal.equals(passwordGiven);
    }

    private void addIdentityCookie(String identityKey ,HttpServletResponse response){
        if(response != null) {
            response.addHeader("Set-Cookie", cookieGenerator(identityKey));
        }
    }

    private String cookieGenerator(String identityKey){
        return settingsService.authKey + "=" + identityKey + "; SameSite=Strict; Path=/; Max-Age=15000000; HttpOnly;";
    }

    private Optional<User> findById(long userId){
        for(User user : users){
            if(user.getId() == userId) return Optional.of(user);
        }
        return Optional.empty();
    }

    private List<User> findUsersByStringContainedInTheName(String userName){
        List<User> users = new ArrayList<>();
        for(User user : this.users){
            if(user.getName().contains(userName)) {
                users.add(user);
            }
        }
        return users;
    }

    private long generateId(){
        if(users.isEmpty()) return 0;
        else {
            return users.get(users.size()-1).getId() + 1;
        }
    }
}
