FROM openjdk:11-jre-slim

MAINTAINER keydo.tistory.com

ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} app.jar

RUN mkdir -p /pinpoint-agent
COPY pinpoint-agent-2.4.1 /pinpoint-agent

ENTRYPOINT exec java ${JAVA_OPTS} -jar app.jar
