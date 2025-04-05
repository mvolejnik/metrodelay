FROM azul/zulu-openjdk-alpine:24.0.0-24.28-jre-headless

RUN apk -U add perf 
ADD remote-resources-uber.jar /

RUN addgroup -g 1000 java 
RUN adduser -D -h /home/java -s /bin/sh -G java -u 1000 java

USER java
WORKDIR /

ENTRYPOINT ["java", "-jar", "remote-resources-uber.jar"]
