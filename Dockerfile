# 多阶段构建 - 第一阶段：构建应用
FROM maven:3.8.6-openjdk-17 AS builder

# 设置工作目录
WORKDIR /app

# 复制项目文件
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# 复制源代码
COPY src src

# 构建应用程序
RUN ./mvnw clean package -DskipTests

# 第二阶段：运行应用
FROM openjdk:17-jre-slim

# 安装时区数据（可选）
RUN apt-get update && apt-get install -y tzdata && rm -rf /var/lib/apt/lists/*

# 设置工作目录
WORKDIR /app

# 从构建阶段复制JAR文件
COPY --from=builder /app/target/*.jar app.jar

# 创建一个非root用户运行应用（安全最佳实践）
RUN addgroup --system --gid 1001 appuser && \
    adduser --system --uid 1001 --ingroup appuser appuser
USER 1001:1001

# 暴露应用端口（通常为8080）
EXPOSE 8080

# 启动应用
ENTRYPOINT ["java", "-jar", "app.jar"]