package com.apnatime.service;

import com.apnatime.domain.SearchResult;
import org.postgresql.copy.CopyManager;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;

@Service
public class UserServiceImpl implements IUserService{
    @Override
    public void generateRandomData(Integer numberOfUsers, Integer maxNumberOfFriendships) throws Exception {

    }

    @Override
    public Integer addUsersData(MultipartFile multiPartFile) throws Exception {
        File file = convertMultiPartToFile(multiPartFile);
        try{
            Reader reader = new FileReader(file);


        }catch (Exception e){

        }
        return null;
    }

    @Override
    public Integer addConnectionsData() throws Exception {
        return null;
    }

    @Override
    public List<SearchResult> findUserConnectionsByLevel(Integer userId, Integer level) throws Exception {
        return null;
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

}
