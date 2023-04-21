package com.messenger.messenger.controller;

import com.messenger.messenger.model.dto.AuthorizationResponseDto;
import com.messenger.messenger.model.dto.UserDataDto;
import com.messenger.messenger.model.dto.UserDto;
import com.messenger.messenger.model.entity.User;
import com.messenger.messenger.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:4200/",allowCredentials = "true")
@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(value = "/ping")
    public ResponseEntity<Boolean> pingServer(HttpServletRequest request){
        Optional<User> userOptional = userService.findUserByHttpRequest(request);
        if(userOptional.isPresent()){
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.ok(false);
        }
    }

    @PostMapping(value = "/register")
    public AuthorizationResponseDto registerUser(@RequestBody UserDataDto userDataDto){
        return userService.register(userDataDto);
    }

    @PostMapping(value = "/login")
    public AuthorizationResponseDto loginUser(@RequestBody UserDataDto userDataDto, HttpServletResponse response){
        return userService.loginUser(userDataDto, response);
    }

    @GetMapping(value = "/findUser/{userName}")
    public ResponseEntity<List<UserDto>> findUser(@PathVariable String userName, HttpServletRequest request){
        Optional<User> userOptional = userService.findUserByHttpRequest(request);
        if (userOptional.isPresent()) {
            List<User> usersFound = userService.findUsersByNameExcludingUsersAlreadyInDirectConversation(userName, userOptional.get());
            return ResponseEntity.ok(usersFound.stream().map(user -> user.getDto()).collect(Collectors.toList()));
        } else {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @GetMapping(value = "/allUsers")
    public ResponseEntity<List<UserDto>> allUsers(HttpServletRequest request){
        Optional<User> userOptional = userService.findUserByHttpRequest(request);
        if(userOptional.isPresent()){
            return ResponseEntity.ok(userService.getAllUsersContacts(userOptional.get()));
        }
        return ResponseEntity.ok(new ArrayList<>());
    }
}
