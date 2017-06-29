# slime microservice chat example

## Content

 * Vert.x
 * Chat Microservices
 * Message Queue ( Redis Queue )
 * 10,000 peoples in same chat room.
 * Distributed message server
 
## Requirement
 * Maven (https://maven.apache.org/)
 * JDK 1.8 (http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
 * Redis (https://redis.io)
 
## Building

To build the code:

    cd slime-http
    mvn clean package
    java -jar target/slime-http-0.0.1-SNAPSHOT-fat.jar
    
    cd slime-sockjs
    mvn clean package
    java -jar target/slime-sockjs-0.0.1-SNAPSHOT-fat.jar 
    
    == REDIS MQ 사용 == 
    cd slime-publish
    mvn clean package
    java -jar target/slime-publish-0.0.1-SNAPSHOT-fat.jar 

And connect your browser http://localhost:8080/

## Comming Soon other examples


## Reference Project
 - https://github.com/sayseakleng/vertx-eventbus-chat
 - https://github.com/cescoffier/vertx-microservices-workshop
