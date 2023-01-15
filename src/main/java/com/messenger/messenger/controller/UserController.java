package com.messenger.messenger.controller;

import com.messenger.messenger.model.dto.UserDataDto;
import com.messenger.messenger.model.dto.UserDto;
import com.messenger.messenger.model.entity.User;
import com.messenger.messenger.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
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
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping(value = "/register")
    public ResponseEntity<Boolean> registerUser(UserDataDto userDataDto){
        return userService.register(userDataDto);
    }

    @PostMapping(value = "/login")
    public ResponseEntity<Boolean> loginUser(@RequestBody UserDataDto userDataDto, HttpServletRequest request, HttpServletResponse response){
        return userService.loginUser(userDataDto, request, response);
    }

    @PostMapping(value = "/findUser/{userName}")
    public ResponseEntity<List<UserDto>> findUser(HttpServletRequest request, @PathVariable String userName){
        Optional<User> userOptional = userService.findUserByHttpRequest(request);
        if (userOptional.isPresent()) {
            return ResponseEntity.ok(userService.findUsers(userName));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
