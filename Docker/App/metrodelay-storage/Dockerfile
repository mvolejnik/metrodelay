FROM azul/zulu-openjdk-alpine:25.0.1-25.30-jre-headless

RUN apk -U add perf 
ADD metrodelay-storage-uber.jar /

RUN addgroup -g 1000 java 
RUN adduser -D -h /home/java -s /bin/sh -G java -u 1000 java

USER java
WORKDIR /

ENTRYPOINT [ \
    "java", \
    "-XX:+UseG1GC", \
    "-XX:+PrintCodeCache", \
    "-XX:+ExitOnOutOfMemoryError", \
    "-XX:+HeapDumpOnOutOfMemoryError", \
    "-Dcom.sun.management.jmxremote.port=5001", \
    "-Dcom.sun.management.jmxremote.authenticate=false", \
    "-Dcom.sun.management.jmxremote.authenticate=false", \
    "-jar", \
    "metrodelay-storage-uber.jar" \
    ]