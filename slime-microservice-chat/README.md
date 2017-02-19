# slime microservice chat example

## Teasing


## Content

 * Vert.x
 * Chat Microservices
 * Message Queue

## Building

To build the code:

    cd slime-http
    mvn clean package
    java -jar target/slime-http-0.0.1-SNAPSHOT-fat.jar
    
    cd slime-sockjs
    mvn clean package
    java -jar target/slime-sockjs-0.0.1-SNAPSHOT-fat.jar 

And connect your browser http://localhost:8080/