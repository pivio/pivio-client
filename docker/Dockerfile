FROM openjdk:8-jre-alpine

RUN set -x \
    \
    echo "===> Installing bash..." && \
    apk update                     && \
    apk add --no-cache bash


COPY /build/libs/pivio.jar /pivio.jar
COPY /docker/pivio.sh /usr/bin/pivio

RUN chmod +x /usr/bin/pivio

WORKDIR /

VOLUME ["/source"]

CMD ["pivio.sh"]