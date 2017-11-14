FROM openjdk:8-jre-slim

## License checker does not support getting started from a different directory
# Getting prerequisites to npm:
RUN set -x \
    \
    echo "===> Installing license-checker..."                                                           && \
    apt-get update -yq                                                                                  && \
    apt-get upgrade -yq                                                                                 && \
    apt-get install -yq curl git nano gnupg2                                                            && \
    curl -sL https://deb.nodesource.com/setup_6.x | bash && apt-get install -yq nodejs build-essential  && \
    apt-get install npm                                                                                 && \
    npm install license-checker                                                                         && \
    \
    \
    apt-get clean                                                                                       && \
    rm -rf /var/lib/apt/lists/*


COPY /build/libs/pivio.jar /pivio.jar
COPY /docker/pivio.sh /usr/bin/pivio
COPY /docker/npm-license-checker.sh /usr/bin/npm-license-checker

RUN chmod +x /usr/bin/pivio


WORKDIR /

VOLUME ["/source"]

CMD ["pivio"]
