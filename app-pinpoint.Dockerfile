FROM openjdk:11-jre-slim

MAINTAINER keydo.tistory.com

ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["nohup","java","-jar",\
"-javaagent:pinpoint-agent-2.4.1/pinpoint-bootstrap-2.4.1.jar",\
"-Dpinpoint.agentId=ysa01","-Dpinpoint.applicationName=yousinsa",\
"-Dpinpoint.config=pinpoint-agent-2.4.1/pinpoint-root.config"\
,"-Dspring.profiles.active=prod","app.jar","2>&1","&"]
