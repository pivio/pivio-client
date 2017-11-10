FROM openjdk:8-jre-alpine

COPY /build/libs/pivio.jar /pivio.jar
COPY /docker/pivio.sh /usr/bin/pivio

RUN chmod +x /usr/bin/pivio

WORKDIR /

VOLUME ["/source"]

CMD ["pivio", "-help"]