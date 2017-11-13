#FROM openjdk:8-jre-alpine
FROM openjdk:8-jre-slim

#RUN set -x \
#    \
#    echo "===> Installing bash..." && \
#    apk update                     && \
#    apk add --no-cache bash


COPY /build/libs/pivio.jar /pivio.jar
COPY /docker/pivio.sh /usr/bin/pivio
COPY /docker/npm-license-checker.sh /usr/bin/npm-license-checker

RUN chmod +x /usr/bin/pivio

## License checker does not support getting started from a different directory
# Getting prerequisites to npm:
RUN apt-get update -yq && apt-get upgrade -yq && apt-get install -yq curl git nano gnupg2
RUN curl -sL https://deb.nodesource.com/setup_6.x | bash && apt-get install -yq nodejs build-essential
RUN apt-get install npm
RUN npm install license-checker

# To run the license checker execute the following command before



WORKDIR /

VOLUME ["/source"]

CMD ["pivio"]
