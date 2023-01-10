package com.messenger.messenger.service.message;

import com.messenger.messenger.model.dto.MessageDto;
import com.messenger.messenger.model.dto.RequestDto;
import com.messenger.messenger.model.dto.UserDto;
import com.messenger.messenger.model.entity.User;
import com.messenger.messenger.service.user.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    @Autowired
    private UpdateService updateService;

    @Autowired
    private SenderService senderService;

    @Autowired
    private ConversationAdderService conversationAdderService;

    @Autowired
    private AuthorizationService authorizationService;

    public ResponseEntity<List<MessageDto>> update(RequestDto requestDto, HttpServletRequest request){
        Optional<User> userOptional = authorizationService.findUser(request);
        if(userOptional.isPresent()) {
            return ResponseEntity.ok(updateService.update(requestDto, userOptional.get()));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    public ResponseEntity<Boolean> send(MessageDto messageDto, HttpServletRequest request){
        Optional<User> userOptional = authorizationService.findUser(request);
        if(userOptional.isPresent()) {
            return ResponseEntity.ok(senderService.send(messageDto, userOptional.get()));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    public ResponseEntity<String> addConversation(HttpServletRequest request, List<UserDto> userDtos){
        Optional<User> userOptional = authorizationService.findUser(request);
        if(userOptional.isPresent()) {
            return ResponseEntity.ok(conversationAdderService.addConversation(userDtos, userOptional.get()));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
