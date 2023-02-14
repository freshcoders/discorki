FROM maven:latest AS maven
WORKDIR /usr/src/app
COPY . /usr/src/app
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
ARG JAR_FILE=discorki.jar
WORKDIR /opt/app
COPY --from=maven /usr/src/app/target/${JAR_FILE} /opt/app/
ENTRYPOINT ["java","-jar","discorki.jar"]
