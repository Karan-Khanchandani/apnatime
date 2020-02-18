package com.apnatime.service;

import com.apnatime.domain.Pair;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements IUserService{

    @Value("${databaseServer}")
    private String dataSourceUrl;

    @Value("${databaseUsername}")
    private String dataSourceUsername;

    @Value("${databasePassword}")
    private String dataSourcePassword;


    @Override
    public void generateRandomData(Integer numberOfUsers, Integer maxNumberOfFriendships){
        try{
            PgConnection pgConnection = (PgConnection) getDatabaseConnection();

            //first truncate the user table
            String sql = "truncate table users cascade";

            pgConnection.execSQLUpdate(sql) ;

            sql = "insert into users(user_id, name)" +
                    " select g1.x, substr(md5(random()::text), 0, 6)" +
                    " from generate_series(1, " + numberOfUsers+ ") as g1(x)";

            pgConnection.execSQLUpdate(sql);


            StringBuilder sb = new StringBuilder();
            Set<Pair> friendshipPairs = new HashSet<>();

            for(int i = 1; i <= numberOfUsers; i++){
                //can have zero friends, so floor
                int firstUser = i;
                int friendLimitForithUser = (int) Math.floor(Math.random()*maxNumberOfFriendships);
                for(int j = 0; j < friendLimitForithUser; j++){
                    int secondUser = (int)Math.floor(Math.random()*numberOfUsers) + 1;
                    if(firstUser != secondUser){
                        //need to add both relations in table, we can discuss on this further
                        friendshipPairs.add(new Pair(firstUser, secondUser));
                        friendshipPairs.add(new Pair(secondUser, firstUser));
                    }
                }
            }

            for(Pair p : friendshipPairs){
                sb.append(p.getFirst() + "," + p.getSecond());
                sb.append("\n");
            }

            String csvData = sb.toString();
            BufferedReader bufferedReader = new BufferedReader(new StringReader(csvData));
            CopyManager copyManager = pgConnection.getCopyAPI();
            long recordsInserted = copyManager.copyIn("COPY user_friends_mapping(user_id, friend_id) FROM STDIN WITH DELIMITER AS ','",bufferedReader);
            System.out.println(recordsInserted);

            pgConnection.close();

        }catch (Exception e){
            System.out.println("ss");
            e.printStackTrace();
        }
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
