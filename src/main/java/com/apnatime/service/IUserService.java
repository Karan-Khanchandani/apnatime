package com.apnatime.service;

import com.apnatime.domain.SearchResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IUserService {

    void generateRandomData(Integer numberOfUsers, Integer maxNumberOfFriendships) throws Exception;

    Integer addUsersData(MultipartFile multiPartFile) throws Exception;

    Integer addConnectionsData() throws  Exception;

    List<SearchResult> findUserConnectionsByLevel(Integer userId, Integer level) throws Exception;
}
