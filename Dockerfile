
# Etape 1 : construire le projet avec Maven
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copier le fichier Maven
COPY pom.xml .

# Télécharger les dépendances
RUN mvn -q dependency:go-offline

# Copier le code source Java
COPY src ./src

# Compiler et générer le fichier JAR
RUN mvn -q package -DskipTests

# Etape 2 : exécuter l'application
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copier le JAR généré
COPY --from=build /app/target/gros-backend.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
