FROM eclipse-temurin:17-jdk-jammy
VOLUME /tmp
USER 0
RUN mkdir -p /cdn-content
USER $CONTAINER_USER_ID
COPY build/libs/mock-cdn-0.0.1.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]