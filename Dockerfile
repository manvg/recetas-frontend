FROM eclipse-temurin:22-jdk AS buildstage

RUN apt-get update && apt-get install -y maven

WORKDIR /app

COPY pom.xml .
COPY src /app/src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:22-jdk

COPY --from=buildstage /app/target/recetas-0.0.1-SNAPSHOT.jar /app/recetas.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app/recetas.jar"]

# Para docker, pasar variable de entorno como JWT_SECRET al ejecutar el contenedor
# Ejemplo: docker run -e JWT_SECRET=secreto_secreto -p 8081:8081 <imagen>
