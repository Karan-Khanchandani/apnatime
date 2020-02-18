FROM java:8
COPY /build/libs/com.apnatime-1.0-SNAPSHOT.jar com.apnatime-1.0-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","com.apnatime-1.0-SNAPSHOT.jar"]