
The application can be used to find Nth degree connections in a friend circle.
It uses postgres to store the user and relationships information and takes advantage of postgres recursive queries
to find the friends, friends of friends and so on.

## Dependencies
You should have the following dependencies installed in the system
* Gradle
* Docker with docker compose


## Running the app
There are different approaches through which we can run the application
###  Run locally
You should have Postgres and Java installed to run the application.

Run the following commands

```
./gradlew clean build
cd build/libs
java -jar -Dspring.profiles.active=local com.apnatime-1.0-SNAPSHOT.jar
```
### Docker compose
You can use this option if you don't want to install all the dependencies(Java, Postgres). Before running the docker compose command make sure you build the project.

Docker compose file `launch_app.yml` contains all the dependencies required for the application.

It also contains pgAdmin which is a web UI for viewing postgres database.

Run the following commands
```
./gradlew clean build
docker-compose -f launch_app.yml up --build
```
You can then use the REST APIs in the same way for the local run to interact with application.

To establish connection between pgAdmin and postgres, go through the [following guide](https://info.crunchydata.com/blog/easy-postgresql-10-and-pgadmin-4-setup-with-docker) 


## Running tests

Run the following command
```
./gradlew clean test
```


## Rest APIs
The following REST APIs are provided by Application

### Generate Random data
Generates random seed data for the database. It first truncates the table and then generate the data.
It takes two params 

* number_of_users - Total number of users
* max_number_of_friendships - Maximum number of friendships per user

#### Request

```
curl --location --request POST 'localhost:8080/generateRandomData' \
--header 'Content-Type: application/json' \
--data-raw '{
	"number_of_users": 10,
	"max_number_of_friendships": 5
}'
```

#### Response
```
{
    "error_message": "",
    "is_successful": true
}
```

### Upload User Information
Upload a csv file which adds the records in the database.

Uses `COPY` command in postgres for fast uploads.

Csv structure is as follows

| user_id | name |
| --- | --- |
| 1 | A|
| 2 | B|

Following caution should be taken:
* CSV should have the headers as well
* CSV must not contain duplicate user_id
* Should be uploaded first before uploading relations CSV data
#### Request

```
curl --location --request POST 'localhost:8080/uploadUsersInformation' \
--header 'Content-Type: multipart/form-data; boundary=--------------------------605934953827895147955205' \
--form 'file=@/Users/karankhanchandani/Desktop/names.csv'
```

#### Response
```
{
    "error_message": "ERROR: duplicate key value violates unique constraint \"users_pkey\"\n  Detail: Key (user_id)=(1) already exists.\n  Where: COPY users, line 2",
    "is_successful": false
}
```

### Upload Relations Data
Upload a csv file which adds the records in the database.

Uses `COPY` command in postgres for fast uploads.

Csv structure is as follows

| user_id | friend_id |
| --- | --- |
| 1 | 2|
| 2 | 1|
| . | .|
| . | .|

Following caution should be taken:
* CSV should have the headers as well
* CSV must not contain duplicate user_id, friend_id combo
* Should be uploaded after uploading users CSV data
* Should have both relations, i.e, if 1 is friend of 2, then both (1,2) and (2,1) should be present
#### Request

```
curl --location --request POST 'localhost:8080/uploadConnectionsInformation' \
--header 'Content-Type: multipart/form-data; boundary=--------------------------148058578770827735438964' \
--form 'file=@/Users/karankhanchandani/Desktop/rel.csv'
```

#### Response
```
{
    "error_message": "",
    "number_of_records_added": 18,
    "is_successful": true
}
```

### Search Connections
Searches for relations in database for a particular user and a level and returns list ordered based on the number of mutual connections.
It takes two params 

* userId - User ID of person for which we are finding connections
* level - Level of connection (1st is friends, 2nd is friends of friends)

#### Request

```
curl --location --request GET 'localhost:8080/search?userId=9&level=3' \
--header 'Content-Type: application/json' \
--data-raw ''
```

#### Response
```
[
    {
        "user": {
            "user_id": "1",
            "name": "7c67d",
            "creation_date": null
        },
        "number_of_mutual_connections": "2",
        "level": "3"
    },
    {
        "user": {
            "user_id": "2",
            "name": "c4b1e",
            "creation_date": null
        },
        "number_of_mutual_connections": "2",
        "level": "3"
    },
    {
        "user": {
            "user_id": "3",
            "name": "9230f",
            "creation_date": null
        },
        "number_of_mutual_connections": "1",
        "level": "3"
    }
]
```

