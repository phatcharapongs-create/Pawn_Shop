FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY . .
# เพิ่ม RUN chmod +x mvnw เพื่อป้องกันปัญหา permission บน Linux Cloud
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
# เปลี่ยน ENTRYPOINT เพื่อเปิดทางให้ใช้ $JAVA_OPTS ได้จริง
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]