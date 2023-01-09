package com.messenger.messenger.service.message;

import com.messenger.messenger.model.dto.MessageDto;
import com.messenger.messenger.model.dto.RequestDto;
import com.messenger.messenger.model.dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class MessageService {

    public ResponseEntity<List<MessageDto>> update(RequestDto requestDto, HttpServletRequest request){

    }

    public boolean send(MessageDto messageDto, HttpServletRequest request){

    }

    public ResponseEntity<String> addConversation(HttpServletRequest request, List<UserDto> userDtos){

    }
}
