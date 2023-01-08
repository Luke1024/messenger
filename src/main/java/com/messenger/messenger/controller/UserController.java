package com.messenger.messenger.controller;

import com.messenger.messenger.model.dto.UserDataDto;
import com.messenger.messenger.model.dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/user")
public class UserController {

    @GetMapping(value = "/ping")
    public ResponseEntity<Boolean> pingServer(HttpServletRequest request){

    }

    @PostMapping(value = "/register")
    public ResponseEntity<Boolean> registerUser(UserDataDto userDataDto){

    }

    @PostMapping(value = "/login")
    public ResponseEntity<Boolean> loginUser(@RequestBody UserDataDto userDataDto, HttpServletRequest request, HttpServletResponse response){

    }

    @PostMapping(value = "/findUser/{userName}")
    public ResponseEntity<List<UserDto>> findUser(HttpServletRequest request, @PathVariable String userName){

    }
}
