#build
FROM eclipse-temurin:17 as build

#WORKDIR /app

COPY .. .

RUN chmod +x ./mvnw
RUN sed -i 's/\r$//' mvnw

RUN ./mvnw clean install
RUN ./mvnw jar:jar


#package
FROM eclipse-temurin:17

COPY --from=build target/Mugloar-*.jar Mugloar.jar

ENTRYPOINT ["java", "-jar", "Mugloar.jar"]
