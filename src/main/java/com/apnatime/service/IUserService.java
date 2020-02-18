package com.apnatime.service;

import com.apnatime.domain.SearchResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IUserService {

    void generateRandomData(Integer numberOfUsers, Integer maxNumberOfFriendships) throws Exception;

    long addUsersData(MultipartFile multiPartFile) throws Exception;

    long addConnectionsData(MultipartFile multiPartFile) throws  Exception;

    List<SearchResult> findUserConnectionsByLevel(Integer userId, Integer level) throws Exception;
}
