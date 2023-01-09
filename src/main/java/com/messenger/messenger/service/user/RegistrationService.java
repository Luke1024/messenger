package com.messenger.messenger.service.user;

import com.messenger.messenger.model.dto.UserDataDto;
import com.messenger.messenger.model.entity.User;
import com.messenger.messenger.repository.UserRepository;
import com.messenger.messenger.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
class RegistrationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    public ResponseEntity<Boolean> register(UserDataDto userDataDto){
        if(isRegistrationAllowed(userDataDto)){
            registerNewUser(userDataDto);
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.badRequest().body(false);
        }
    }

    private boolean isRegistrationAllowed(UserDataDto userDataDto){
        Optional<User> userOptional = userRepository.findByName(userDataDto.getUserName());
        if(userOptional.isPresent()){
            return false;
        } else return true;
    }

    private void registerNewUser(UserDataDto userDataDto){
        userRepository.save(generateNewUser(userDataDto));
    }

    private User generateNewUser(UserDataDto userDataDto){
        return new User(userDataDto.getUserName(), userDataDto.getPassword(), generateIdentityKey());
    }

    private String generateIdentityKey(){
        return tokenService.generate();
    }
}
