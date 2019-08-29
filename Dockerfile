FROM openjdk:8
VOLUME /tmp
ADD target/blackonwhite-0.0.1-SNAPSHOT.jar blackonwhite-0.0.1-SNAPSHOT.jar
EXPOSE 8080
CMD ["java","-jar","blackonwhite-0.0.1-SNAPSHOT.jar"]