# ============================================
# Stage 1: Build the WAR using Maven
# ============================================
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy POM first for better Docker layer caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# ============================================
# Stage 2: Deploy to Tomcat
# ============================================
FROM tomcat:10.1-jdk17-temurin

# Remove default Tomcat webapps
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy WAR as ROOT.war so the app is served at /
COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

# Expose Tomcat's default port
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]
