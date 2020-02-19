package com.apnatime.api;

import com.apnatime.domain.GenerateRandomDataResponse;
import com.apnatime.domain.GenerateRandomDataRequest;
import com.apnatime.domain.SearchResult;
import com.apnatime.domain.UploadDataResponse;
import com.apnatime.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
    public ResponseEntity<?> generateRandomData(@RequestBody GenerateRandomDataRequest generateRandomDataRequest) {
        try {
            userService.generateRandomData(generateRandomDataRequest.getNumberOfUsers(), generateRandomDataRequest.getMaxNumberOfFriendships());

            return new ResponseEntity<>(GenerateRandomDataResponse.builder().errorMessage("").isSuccessful(true).build(), HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>(GenerateRandomDataResponse.builder().errorMessage(e.getMessage()).isSuccessful(false).build(), HttpStatus.BAD_GATEWAY);
        }
    }

    @PostMapping("/uploadUsersInformation")
    public ResponseEntity<?> uploadUserInfomation(@RequestPart(value = "file") MultipartFile multiPartFile) {
        try {
            long recordsInserted = userService.addUsersData(multiPartFile);
            return new ResponseEntity<>(UploadDataResponse.builder().errorMessage("")
                    .isSuccessful(true).numberOfRecordsAdded(recordsInserted).build(), HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>(GenerateRandomDataResponse.builder().errorMessage(e.getMessage()).isSuccessful(false).build(), HttpStatus.BAD_GATEWAY);
        }
    }

    @PostMapping("/uploadConnectionsInformation")
    public ResponseEntity<?> uploadConnectionsInfomation(@RequestPart(value = "file") MultipartFile multiPartFile) {
        try {
            long recordsInserted = userService.addConnectionsData(multiPartFile);
            return new ResponseEntity<>(UploadDataResponse.builder().errorMessage("")
                    .isSuccessful(true).numberOfRecordsAdded(recordsInserted).build(), HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>(GenerateRandomDataResponse.builder().errorMessage(e.getMessage()).isSuccessful(false).build(), HttpStatus.BAD_GATEWAY);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> findUsersAtNLevel(@RequestParam("userId") Integer userId, @RequestParam("level") Integer level) {
        try {
            List<SearchResult> results = userService.findUserConnectionsByLevel(userId, level);
            return new ResponseEntity<>(results, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(GenerateRandomDataResponse.builder().errorMessage(e.getMessage()).isSuccessful(false).build(), HttpStatus.BAD_GATEWAY);
        }
    }
}
