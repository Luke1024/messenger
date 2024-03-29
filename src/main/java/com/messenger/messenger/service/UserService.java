package com.messenger.messenger.service;

import com.messenger.messenger.model.dto.AuthorizationResponseDto;
import com.messenger.messenger.model.dto.UserDataDto;
import com.messenger.messenger.model.dto.UserDto;
import com.messenger.messenger.model.entity.User;
import com.messenger.messenger.service.utils.Settings;
import com.messenger.messenger.service.utils.TokenGenerator;
import com.messenger.messenger.service.utils.userfinder.UserFinder;
import com.messenger.messenger.service.utils.temp.DataGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private TokenGenerator tokenGenerator;

    @Autowired
    private Settings settings;

    @Autowired
    private UserFinder userFinder;

    @Autowired
    private DataGenerator dataGenerator;

    private Logger logger = LoggerFactory.getLogger(UserService.class);

    public List<User> users = new ArrayList<>();

    public List<User> findUsersByNameExcludingUsersAlreadyInDirectConversation(String userName, User userRequesting){
        return userFinder.findUsersByNameExcludingUsersAlreadyInDirectConversation(userName, userRequesting, users);
    }

    public Optional<User> findUserByHttpRequest(HttpServletRequest request){
        return userFinder.findUserByHttpRequest(request, users);
    }

    public AuthorizationResponseDto register(UserDataDto userDataDto){
        if(isRegistrationAllowed(userDataDto)){
            logger.info("Registering user with name: " + userDataDto.getUserName()
                    + " and password: " + userDataDto.getPassword());
            User newUser = generateNewUserFromDataDto(userDataDto);
            newUser.setId(generateId());
            users.add(newUser);

            //this is temporary for development only
            if(userDataDto.getUserName().equals("testUser")) {
                dataGenerator.generateDataForUser(newUser, this);
            }
            return new AuthorizationResponseDto(true,"");
        } else {
            return new AuthorizationResponseDto(false, "Try different user name.");
        }
    }

    public AuthorizationResponseDto loginUser(UserDataDto userDataDto, HttpServletResponse response){
        Optional<User> userOptional = userFinder.findByName(userDataDto.getUserName(), users);
        if(userOptional.isPresent()) {
            if (isPasswordValid(userOptional.get().getPassword(),userDataDto.getPassword())) {
                addIdentityCookie(userOptional.get().getIdentityKey(),response);
                return new AuthorizationResponseDto(true, "");
            }
        }
        return new AuthorizationResponseDto(false, "Login credentials are incorrect.");
    }

    public List<User> findUsersByDto(List<UserDto> userDtos){
        return userFinder.findUsersByDto(userDtos, users);
    }

    public List<UserDto> getAllUsersContacts(User userRequesting){
        List<User> usersBelongingToUser = userRequesting.getConversations()
                .entrySet().stream()
                .map(entry -> entry.getKey())
                .filter(conversation -> conversation.isDirect())
                .map(conversation -> conversation.getUsersInConversation())
                .flatMap(Collection::stream)
                .filter(user -> user != userRequesting).collect(Collectors.toList());
        return usersBelongingToUser.stream().map(user -> user.getDto()).collect(Collectors.toList());
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
        return tokenGenerator.generate();
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
        return settings.authKey + "=" + identityKey + "; SameSite=Strict; Path=/; Max-Age=15000000; HttpOnly;";
    }

    private long generateId(){
        if(users.isEmpty()) return 0;
        else {
            return users.get(users.size()-1).getId() + 1;
        }
    }
}
