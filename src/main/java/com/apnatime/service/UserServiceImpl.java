package com.apnatime.service;

import com.apnatime.domain.SearchResult;
import org.hibernate.Session;
import org.postgresql.copy.CopyManager;
import org.postgresql.ds.PGConnectionPoolDataSource;
import org.postgresql.jdbc.PgConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Service
public class UserServiceImpl implements IUserService{

    @Value("${databaseServer}")
    private String dataSourceUrl;

    @Value("${databaseUsername}")
    private String dataSourceUsername;

    @Value("${databasePassword}")
    private String dataSourcePassword;


    @Override
    public void generateRandomData(Integer numberOfUsers, Integer maxNumberOfFriendships) throws Exception {

    }

    @Override
    public long addUsersData(MultipartFile multiPartFile) throws Exception {
        File file = convertMultiPartToFile(multiPartFile);
        try{
            Reader reader = new FileReader(file);
            PgConnection pgConnection = (PgConnection) getDatabaseConnection();
            CopyManager copyManager = pgConnection.getCopyAPI();
            long recordsInserted = copyManager.copyIn("COPY users(user_id, name) FROM STDIN WITH DELIMITER AS ',' CSV HEADER",reader);
            return recordsInserted;

        }catch (Exception e){

        }
        return 0;
    }

    @Override
    public long addConnectionsData(MultipartFile multiPartFile) throws Exception {
        File file = convertMultiPartToFile(multiPartFile);
        try{
            Reader reader = new FileReader(file);
            PgConnection pgConnection = (PgConnection) getDatabaseConnection();
            CopyManager copyManager = pgConnection.getCopyAPI();
            long recordsInserted = copyManager.copyIn("COPY user_friends_mapping(user_id, friend_id) FROM STDIN WITH DELIMITER AS ',' CSV HEADER",reader);
            return recordsInserted;
        }catch (Exception e){

        }
        return 0;
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

    private Connection getDatabaseConnection(){
        PGConnectionPoolDataSource dataSource = new PGConnectionPoolDataSource();
        dataSource.setUrl("jdbc:"+dataSourceUrl);
        dataSource.setUser(dataSourceUsername);
        dataSource.setPassword(dataSourcePassword);

        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
