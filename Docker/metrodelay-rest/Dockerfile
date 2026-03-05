FROM azul/zulu-openjdk-alpine:25.0.2-jre-headless

RUN addgroup -g 1000 java 
RUN adduser -D -h /home/java -s /bin/sh -G java -u 1000 java

RUN apk update \
  && apk upgrade \
  && apk add jattach \
  && apk add perf 

ADD metrodelay-rest-uber.jar /

USER java
WORKDIR /

ENTRYPOINT [ \
    "sh", \
    "-c", \
    "exec java -XX:+UseG1GC -XX:+PrintCodeCache -XX:+ExitOnOutOfMemoryError -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/var/log/heapdumps -Dcom.sun.management.jmxremote.port=5001 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.authenticate=false $JAVA_OPTS -jar metrodelay-rest-uber.jar" \
    ]