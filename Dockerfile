FROM ubuntu:latest

# Java
RUN apt-get update
RUN apt-get install -y curl unzip openjdk-21-jdk
RUN rm -rf /var/lib/apt/lists/*
RUN java -version

RUN apt-get update
RUN apt-get install -y curl unzip
RUN rm -rf /var/lib/apt/lists/*

# Kotlin
ENV KOTLIN_VERSION=1.9.22
RUN curl -LO https://github.com/JetBrains/kotlin/releases/download/v${KOTLIN_VERSION}/kotlin-compiler-${KOTLIN_VERSION}.zip
RUN unzip kotlin-compiler-${KOTLIN_VERSION}.zip -d /opt
RUN rm -f kotlin-compiler-${KOTLIN_VERSION}.zip
ENV PATH=$PATH:/opt/kotlinc/bin
RUN kotlinc -version

# Maven
ENV MAVEN_VERSION=3.8.8
ENV MAVEN_HOME=/opt/apache-maven-${MAVEN_VERSION}
ENV PATH=$PATH:$MAVEN_HOME/bin
RUN curl -LO https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz
RUN tar -xzf apache-maven-${MAVEN_VERSION}-bin.tar.gz -C /opt
RUN rm -f apache-maven-${MAVEN_VERSION}-bin.tar.gz

WORKDIR /app

COPY src /app/src
COPY pom.xml /app/

CMD mvn clean install && \
    java -jar target/Game-server-1.0-SNAPSHOT.jar
