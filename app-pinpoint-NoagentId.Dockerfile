FROM openjdk:11-jre-slim

MAINTAINER keydo.tistory.com

ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} app.jar

RUN mkdir -p /pinpoint-agent
COPY pinpoint-agent-2.4.1 /pinpoint-agent

ENTRYPOINT ["java","-jar",\
"-javaagent:pinpoint-agent/pinpoint-bootstrap-2.4.1.jar",\
"-Dpinpoint.applicationName=yousinsa",\
"-Dpinpoint.config=pinpoint-agent/pinpoint-root.config"\
,"-Dspring.profiles.active=prod","app.jar","2>&1","&"]
