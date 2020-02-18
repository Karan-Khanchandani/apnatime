package com.apnatime.api;

import com.apnatime.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class UsersController {

    private final Logger logger = LoggerFactory.getLogger(UsersController.class);

    private final IUserService userService;

    public UsersController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/hello")
    public String getHello() {
        return "Hello world!";
    }

    @PostMapping("/uploadUsersInformation")
    public void uploadUserInfomation(@RequestPart(value = "file") MultipartFile multiPartFile) throws IOException {
        try {
            userService.addUsersData(multiPartFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
