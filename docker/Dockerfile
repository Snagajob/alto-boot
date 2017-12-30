FROM openjdk:8u131-jdk-alpine

COPY . /usr/local/alto-boot

EXPOSE 8080 

WORKDIR /usr/local/alto-boot
RUN apk add --no-cache bash && ./gradlew assemble

CMD SPRING_PROFILES_ACTIVE=docker ./gradlew bootRun
