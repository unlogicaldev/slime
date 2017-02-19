# slime microservice chat example

## Teasing


## Content

 * Vert.x
 * Chat Microservices
 * Message Queue
 * 10,000 peoples in same chat room.
 * Distributed message server

## Building

To build the code:

    cd slime-http
    mvn clean package
    java -jar target/slime-http-0.0.1-SNAPSHOT-fat.jar
    
    cd slime-sockjs
    mvn clean package
    java -jar target/slime-sockjs-0.0.1-SNAPSHOT-fat.jar 

And connect your browser http://localhost:8080/

## Comming Soon other examples


## Reference Project
 - https://github.com/sayseakleng/vertx-eventbus-chat
 - https://github.com/cescoffier/vertx-microservices-workshop