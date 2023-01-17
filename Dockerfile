FROM maven:latest AS maven
WORKDIR /usr/src/app
COPY . /usr/src/app
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk-alpine
ARG JAR_FILE=discorki.jar
WORKDIR /opt/app
COPY --from=maven /usr/src/app/target/${JAR_FILE} /opt/app/
RUN wget https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar
ENTRYPOINT ["java","-Dotel.service.name=jaeger","-Dotel.traces.exporter=jaeger","-Dotel.jaeger.endpoint=http://localhost:14250","-javaagent:/opt/app/opentelemetry-javaagent.jar","-jar","discorki.jar"]
