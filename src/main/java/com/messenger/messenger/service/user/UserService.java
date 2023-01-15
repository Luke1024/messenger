package com.messenger.messenger.service.user;

import com.messenger.messenger.model.dto.UserDataDto;
import com.messenger.messenger.model.dto.UserDto;
import com.messenger.messenger.model.entity.User;
import com.messenger.messenger.service.SettingsService;
import com.messenger.messenger.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private SettingsService settingsService;

    private List<User> users = new ArrayList<>();

    public Optional<User> findUserByHttpRequest(HttpServletRequest request){
        Optional<String> optionalIdentityKey = getIdentityKey(request);
        if(optionalIdentityKey.isPresent()) {
            return findByIdentityKey(optionalIdentityKey.get());
        } else {
            return Optional.empty();
        }
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

    public ResponseEntity<Boolean> register(UserDataDto userDataDto){
        if(isRegistrationAllowed(userDataDto)){
            registerNewUser(userDataDto);
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.badRequest().body(false);
        }
    }

    private boolean isRegistrationAllowed(UserDataDto userDataDto){
        Optional<User> userOptional = findByName(userDataDto.getUserName());
        if(userOptional.isPresent()){
            return false;
        } else return true;
    }

    private void registerNewUser(UserDataDto userDataDto){
        save(generateNewUser(userDataDto));
    }

    private User generateNewUser(UserDataDto userDataDto){
        return new User(userDataDto.getUserName(), userDataDto.getPassword(), generateIdentityKey());
    }

    private String generateIdentityKey(){
        return tokenService.generate();
    }

    public ResponseEntity<Boolean> loginUser(UserDataDto userDataDto, HttpServletRequest request, HttpServletResponse response){
        Optional<User> userOptional = findByName(userDataDto.getUserName());
        if(userOptional.isPresent()) {
            if (isPasswordValid(userOptional.get().getPassword(),userDataDto.getPassword())) {
                addIdentityCookie(userOptional.get().getIdentityKey(),response);
                return ResponseEntity.ok(true);
            }
        }
        return ResponseEntity.badRequest().body(false);
    }

    private boolean isPasswordValid(String passwordReal, String passwordGiven){
        return passwordReal.equals(passwordGiven);
    }

    private void addIdentityCookie(String identityKey ,HttpServletResponse response){
        //add cookie
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

    private Optional<User> findById(long userId){
        for(User user : users){
            if(user.getId() == userId) return Optional.of(user);
        }
        return Optional.empty();
    }

    private List<User> findUsersByStringContainedInTheName(String userName){
        List<User> users = new ArrayList<>();
        for(User user : users){
            if(user.getName().contains(userName)) {
                users.add(user);
            }
        }
        return users;
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

    public void save(User user){
        user.setId(generateId());
        users.add(user);
    }

    private long generateId(){
        if(users.isEmpty()) return 0;
        else {
            return users.get(users.size()-1).getId() + 1;
        }
    }
}
