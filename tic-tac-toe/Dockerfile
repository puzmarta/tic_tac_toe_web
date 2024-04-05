FROM openjdk:17-jdk-alpine
ARG JAR_FILE=build/libs/tic-tac-toe-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} tic-tac-toe.jar
ENTRYPOINT ["java", "-jar", "tic-tac-toe.jar"]