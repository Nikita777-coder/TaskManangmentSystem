FROM openjdk:17
COPY ./target /tmp
WORKDIR /tmp
ENTRYPOINT ["java","-jar","TaskManangmentSystem-1.0-SNAPSHOT.jar"]