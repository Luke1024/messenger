package com.messenger.messenger.service;

import com.messenger.messenger.model.dto.UserDataDto;
import com.messenger.messenger.model.dto.UserDto;
import com.messenger.messenger.model.entity.User;
import com.messenger.messenger.service.utils.UserFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private UserFinder userFinder;

    private Logger logger = LoggerFactory.getLogger(UserService.class);

    private List<User> users = new ArrayList<>();

    public List<UserDto> findUsersByNameToDto(String userName){
        return userFinder.findUsersByNameToDto(userName, users);
    }

    public Optional<User> findUserByHttpRequest(HttpServletRequest request){
        return userFinder.findUserByHttpRequest(request, users);
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
        Optional<User> userOptional = userFinder.findByName(userDataDto.getUserName(), users);
        if(userOptional.isPresent()) {
            if (isPasswordValid(userOptional.get().getPassword(),userDataDto.getPassword())) {
                addIdentityCookie(userOptional.get().getIdentityKey(),response);
                return true;
            }
        }
        return false;
    }

    public List<User> findUsersByDto(List<UserDto> userDtos){
        return userFinder.findUsersByDto(userDtos, users);
    }

    public boolean addUserToUser(User user ,UserDto userDto){
        List<User> userList = user.getUsersWithKeyAsDefaultConversation().entrySet().stream().map(u -> u.getValue()).collect(Collectors.toList());
        for(User userFound : userList){
            if(userFound.getId()==userDto.getUserId()){
                return false;
            }
        }
        Optional<User> optionalUser = userFinder.findById(userDto.getUserId(), users);
        if(optionalUser.isPresent()) {
            user.getUsersWithKeyAsDefaultConversation().put(-1L, optionalUser.get());
            return true;
        }
        return false;
    }

    private boolean isRegistrationAllowed(UserDataDto userDataDto){
        Optional<User> userOptional = userFinder.findByName(userDataDto.getUserName(), users);
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

    private long generateId(){
        if(users.isEmpty()) return 0;
        else {
            return users.get(users.size()-1).getId() + 1;
        }
    }
}
