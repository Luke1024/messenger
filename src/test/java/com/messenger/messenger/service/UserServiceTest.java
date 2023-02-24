package com.messenger.messenger.service;

import com.messenger.messenger.model.dto.UserDataDto;
import com.messenger.messenger.model.entity.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.Cookie;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private SettingsService settingsService;

    @Test
    public void findUserByHttpRequest() throws NoSuchFieldException, IllegalAccessException {
        Field users = userService.getClass().getDeclaredField("users");

        users.setAccessible(true);

        List<User> userList = (List<User>) users.get(userService);

        userList.add(new User("newUser", "newPassword", "identityKey"));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie(settingsService.authKey,"identityKey"));

        Assert.assertTrue(userService.findUserByHttpRequest(request).isPresent());

        userList.clear();
    }

    private String cookieGenerator(String identityKey){
        return settingsService.authKey + "=" + identityKey + "; Max-Age=15000000; Secure; HttpOnly; SameSite=None";
    }

    @Test
    public void registerUser() throws NoSuchFieldException, IllegalAccessException {
        UserDataDto newUserDataDto = new UserDataDto("newUser", "newPassword");

        userService.register(newUserDataDto);
        //check if user with the same name could be registered again
        Assert.assertFalse(userService.register(newUserDataDto));

        Field users = userService.getClass().getDeclaredField("users");

        users.setAccessible(true);
        List<User> userList = (List<User>) users.get(userService);

        Assert.assertEquals("newUser", userList.get(0).getName());

        userList.clear();
    }

    @Test
    public void loginUser() throws NoSuchFieldException, IllegalAccessException {
        UserDataDto newUserDataDto = new UserDataDto("newUser", "newPassword");

        MockHttpServletResponse response = new MockHttpServletResponse();

        //login without register
        Assert.assertFalse(userService.loginUser(newUserDataDto,response));
        //register
        Assert.assertTrue(userService.register(newUserDataDto));
        Assert.assertTrue(userService.loginUser(newUserDataDto, response));

        Assert.assertTrue(response.getCookie(settingsService.authKey) != null);

        Field userList = userService.getClass().getDeclaredField("users");
        userList.setAccessible(true);
        userList.set(userService, new ArrayList<>());
    }

    @Test
    public void findUsersByMultipleMethods() throws NoSuchFieldException, IllegalAccessException {
        UserDataDto userDataDto1 = new UserDataDto("newUser1", "newPassword1");
        UserDataDto userDataDto2 = new UserDataDto("newUser2", "newPassword2");
        UserDataDto userDataDto3 = new UserDataDto("newUser3", "newPassword3");

        userService.register(userDataDto1);
        userService.register(userDataDto2);
        userService.register(userDataDto3);

        Field users = userService.getClass().getDeclaredField("users");
        users.setAccessible(true);
        List<User> userList = (List<User>) users.get(userService);

        for(User user : userList){
            Assert.assertEquals(user, userService.findUsersByDto(Arrays.asList(user.getDto())).get(0));
            Assert.assertEquals(user, userService.findByName(user.getName()).get());
            Assert.assertEquals(user, userService.findByIdentityKey(user.getIdentityKey()).get());
        }

        Assert.assertEquals(userList.get(0).getDto().getUserName(), userService.findUsers("1").get(0).getUserName());
    }


}