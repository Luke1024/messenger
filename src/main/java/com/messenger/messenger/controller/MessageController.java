package com.messenger.messenger.controller;

import com.messenger.messenger.model.dto.MessageDto;
import com.messenger.messenger.model.dto.RequestDto;
import com.messenger.messenger.model.dto.UpdateDto;
import com.messenger.messenger.model.dto.UserDto;
import com.messenger.messenger.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping(value = "/update")
    public ResponseEntity<UpdateDto> getConversationUpdate(@RequestBody RequestDto requestDto, HttpServletRequest request){
        return messageService.update(requestDto, request);
    }

    @PostMapping(value = "/send")
    public ResponseEntity<Boolean> sendMessage(@RequestBody MessageDto messageDto, HttpServletRequest request){
        return messageService.send(messageDto, request);
    }

    @PostMapping(value = "/addConversation")
    public ResponseEntity<Boolean> addConversation(HttpServletRequest request, @RequestBody List<UserDto> userDtos){
        return messageService.addConversation(request, userDtos);
    }
}
