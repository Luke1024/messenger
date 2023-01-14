package com.messenger.messenger.service.user;

import com.messenger.messenger.model.dto.UserDataDto;
import com.messenger.messenger.model.dto.UserDto;
import com.messenger.messenger.model.entity.User;
import com.messenger.messenger.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private UserRepository userRepository;

    private List<User> users = new ArrayList<>();

    public ResponseEntity<Boolean> ping(HttpServletRequest request){
        return authorizationService.cookieCheck(request);
    }

    public ResponseEntity<Boolean> register(UserDataDto userDataDto){
        return registrationService.register(userDataDto);
    }

    public ResponseEntity<Boolean> loginUser(UserDataDto userDataDto, HttpServletRequest request, HttpServletResponse response){
        return authorizationService.login(userDataDto, request, response);
    }

    public ResponseEntity<List<UserDto>> findUser(HttpServletRequest request, String userName) {
        Optional<User> userOptional = authorizationService.findUser(request);
        if (userOptional.isPresent()) {
            return ResponseEntity.ok(findUsers(userName));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    public List<User> findUsersByDto(List<UserDto> userDtos){
        List<User> users = new ArrayList<>();
        for(UserDto userDto : userDtos){
            Optional<User> optionalUser = userRepository.findById(userDto.getUserId());
            if(optionalUser.isPresent()){
                if(optionalUser.get().getName().equals(userDto.getUserName())){
                    users.add(optionalUser.get());
                }
            }
        }
        return users;
    }

    private List<UserDto> findUsers(String userName){
        List<User> users = userRepository.findUsersByName(userName);
        return users.stream().map(user -> user.getDto()).collect(Collectors.toList());
    }
}
