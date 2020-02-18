package com.apnatime.service;

import com.apnatime.domain.Pair;
import com.apnatime.domain.SearchResult;
import org.postgresql.copy.CopyManager;
import org.postgresql.ds.PGConnectionPoolDataSource;
import org.postgresql.jdbc.PgConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements IUserService {

    @Value("${databaseServer}")
    private String dataSourceUrl;

    @Value("${databaseUsername}")
    private String dataSourceUsername;

    @Value("${databasePassword}")
    private String dataSourcePassword;

    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public void generateRandomData(Integer numberOfUsers, Integer maxNumberOfFriendships) throws Exception {
        PgConnection pgConnection = null;
        try {
            pgConnection = (PgConnection) getDatabaseConnection();
            pgConnection.setAutoCommit(false);

            //first truncate the user table
            String sql = "truncate table users cascade";

            pgConnection.execSQLUpdate(sql);

            sql = "insert into users(user_id, name)" +
                    " select g1.x, substr(md5(random()::text), 0, 6)" +
                    " from generate_series(1, " + numberOfUsers + ") as g1(x)";

            pgConnection.execSQLUpdate(sql);

            logger.info("{} number of users inserted successfully", numberOfUsers);

            StringBuilder sb = new StringBuilder();
            Set<Pair> friendshipPairs = new HashSet<>();

            for (int i = 1; i <= numberOfUsers; i++) {
                //can have zero friends, so floor
                int firstUser = i;
                int friendLimitForithUser = (int) Math.floor(Math.random() * maxNumberOfFriendships);
                for (int j = 0; j < friendLimitForithUser; j++) {
                    int secondUser = (int) Math.floor(Math.random() * numberOfUsers) + 1;
                    if (firstUser != secondUser) {
                        //need to add both relations in table, we can discuss on this further
                        friendshipPairs.add(new Pair(firstUser, secondUser));
                        friendshipPairs.add(new Pair(secondUser, firstUser));
                    }
                }
            }

            for (Pair p : friendshipPairs) {
                sb.append(p.getFirst() + "," + p.getSecond());
                sb.append("\n");
            }

            String csvData = sb.toString();
            BufferedReader bufferedReader = new BufferedReader(new StringReader(csvData));
            CopyManager copyManager = pgConnection.getCopyAPI();

            long recordsInserted = copyManager.copyIn("COPY user_friends_mapping(user_id, friend_id) FROM STDIN WITH DELIMITER AS ','", bufferedReader);

            logger.info("{} number of relationships added", recordsInserted);

            pgConnection.commit();

        } catch (Exception e) {
            logger.error("Exception occurred while generating seed data", e);
            pgConnection.rollback();
            throw e;
        } finally {
            pgConnection.close();
        }
    }

    @Override
    public long addUsersData(MultipartFile multiPartFile) throws Exception {
        PgConnection pgConnection = null;
        try {
            File file = convertMultiPartToFile(multiPartFile);
            Reader reader = new FileReader(file);
            pgConnection = (PgConnection) getDatabaseConnection();
            CopyManager copyManager = pgConnection.getCopyAPI();
            long recordsInserted = copyManager.copyIn("COPY users(user_id, name) FROM STDIN WITH DELIMITER AS ',' CSV HEADER", reader);
            return recordsInserted;

        } catch (Exception e) {
            logger.error("Exception occurred while generating user data from CSV", e);
            throw e;
        } finally {
            pgConnection.close();
        }
    }

    @Override
    public long addConnectionsData(MultipartFile multiPartFile) throws Exception {
        PgConnection pgConnection = null;
        try {
            File file = convertMultiPartToFile(multiPartFile);
            Reader reader = new FileReader(file);
            pgConnection = (PgConnection) getDatabaseConnection();
            CopyManager copyManager = pgConnection.getCopyAPI();
            long recordsInserted = copyManager.copyIn("COPY user_friends_mapping(user_id, friend_id) FROM STDIN WITH DELIMITER AS ',' CSV HEADER", reader);
            return recordsInserted;

        } catch (Exception e) {
            logger.error("Exception occurred while generating relations data from CSV", e);
            throw e;
        } finally {
            pgConnection.close();
        }
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

    private Connection getDatabaseConnection() throws SQLException {
        PGConnectionPoolDataSource dataSource = new PGConnectionPoolDataSource();
        dataSource.setUrl("jdbc:" + dataSourceUrl);
        dataSource.setUser(dataSourceUsername);
        dataSource.setPassword(dataSourcePassword);
        return dataSource.getConnection();
    }

}
