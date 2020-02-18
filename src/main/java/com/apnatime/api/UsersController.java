package com.apnatime.api;

import com.apnatime.domain.GenerateRandomDataRequest;
import com.apnatime.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping("/generateRandomData")
    public ResponseEntity<?> generateRandomData(@RequestBody GenerateRandomDataRequest generateRandomDataRequest) throws IOException {
        try {
            userService.generateRandomData(generateRandomDataRequest.getNumberOfUsers(), generateRandomDataRequest.getMaxNumberOfFriendships());
            return new ResponseEntity<>("OK", HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>("Failed to perform action", HttpStatus.BAD_GATEWAY);
        }
    }

    @PostMapping("/uploadUsersInformation")
    public ResponseEntity<?> uploadUserInfomation(@RequestPart(value = "file") MultipartFile multiPartFile) throws IOException {
        try {
            long recordsInserted = userService.addUsersData(multiPartFile);
            return new ResponseEntity<>(recordsInserted, HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>("Failed to perform action", HttpStatus.BAD_GATEWAY);
        }
    }

    @PostMapping("/uploadConnectionsInformation")
    public ResponseEntity<?> uploadConnectionsInfomation(@RequestPart(value = "file") MultipartFile multiPartFile) throws IOException {
        try {
            long recordsInserted = userService.addConnectionsData(multiPartFile);
            return new ResponseEntity<>(recordsInserted, HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>("Failed to perform action", HttpStatus.BAD_GATEWAY);
        }
    }

}
