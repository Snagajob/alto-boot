FROM openjdk:8u131-jdk-alpine

COPY . /usr/local/alto-boot

EXPOSE 8080 

RUN apk add --no-cache bash
WORKDIR /usr/local/alto-boot

RUN ./gradlew assemble 
CMD ./gradlew bootRun
