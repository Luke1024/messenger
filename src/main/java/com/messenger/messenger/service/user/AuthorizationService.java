package com.messenger.messenger.service.user;

import com.messenger.messenger.model.dto.UserDataDto;
import com.messenger.messenger.model.entity.User;
import com.messenger.messenger.repository.UserRepository;
import com.messenger.messenger.service.TokenService;
import com.messenger.messenger.service.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Service
public class AuthorizationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UrlService urlService;

    @Autowired
    private TokenService tokenService;

    public ResponseEntity<Boolean> cookieCheck(HttpServletRequest request){
        Optional<User> userOptional = findUser(request);
        if(userOptional.isPresent()){
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.ok(false);
        }
    }

    public Optional<User> findUser(HttpServletRequest request){
        Optional<String> optionalIdentityKey = getIdentityKey(request);
        if(optionalIdentityKey.isPresent()) {
            return userRepository.findByIdentityKey(optionalIdentityKey.get());
        } else {
            return Optional.empty();
        }
    }

    private Optional<String> getIdentityKey(HttpServletRequest request){
        if(request.getCookies() != null) {
            Cookie[] cookies = request.getCookies();
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(urlService.authKey)) {
                    return Optional.of(cookie.getValue());
                }
            }
        }
        return Optional.empty();
    }

    public ResponseEntity<Boolean> login(UserDataDto userDataDto, HttpServletRequest request, HttpServletResponse response){
        Optional<User> userOptional = userRepository.findByName(userDataDto.getUserName());
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

}
