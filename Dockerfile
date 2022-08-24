FROM docker.prod.walmart.com/strati/zulu:11-jdk-alpine
USER root

CMD mkdir -p /opt/app
COPY target/*.jar /opt/app/app.jar

EXPOSE 8080

RUN apk add --no-cache fontconfig

RUN ln -sf /usr/lib/libfontconfig.so.1 /usr/lib/libfontconfig.so && \
    ln -sf /lib/libuuid.so.1 /usr/lib/libuuid.so.1 && \
    ln -sf /lib/libc.musl-x86_64.so.1 /usr/lib/libc.musl-x86_64.so.1

RUN apk --update add fontconfig ttf-dejavu

#Change the user id to a non-root user
USER 10000

WORKDIR /opt/app
ENTRYPOINT exec java $JAVA_OPTS -jar /opt/app/app.jar
