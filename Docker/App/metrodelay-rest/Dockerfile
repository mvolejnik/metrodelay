FROM azul/zulu-openjdk-alpine:25.0.1-25.30-jre-headless

RUN apk -U add perf 
ADD metrodelay-rest-uber.jar /

RUN addgroup -g 1000 java 
RUN adduser -D -h /home/java -s /bin/sh -G java -u 1000 java

USER java
WORKDIR /

ENTRYPOINT ["java", "-jar", "metrodelay-rest-uber.jar"]
