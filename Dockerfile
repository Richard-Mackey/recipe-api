FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/RecipeAPI-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", \
    "-Dspring.datasource.url=${DATABASE_URL}", \
    "-Djwt.secret=${JWT_SECRET}", \
    "-Djwt.expiration=86400000", \
    "-Dspoonacular.api.key=${SPOONACULAR_API_KEY}", \
    "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", \
    "-jar", "app.jar"]