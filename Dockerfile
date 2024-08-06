FROM openjdk:21
COPY target/task-management-system-1.0-SNAPSHOT.jar /docker.jar
ENTRYPOINT ["java","-jar","/docker.jar"]