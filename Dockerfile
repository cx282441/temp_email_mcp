# 使用 Maven 官方镜像构建 JAR 文件
FROM maven:3.8.6-openjdk-17-slim AS builder

# 设置时区为 UTC+8
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 设置工作目录
WORKDIR /app

# 拷贝 pom.xml 和源码到容器
COPY pom.xml .
COPY src ./src

# 使用 Maven 构建项目并生成 JAR 包
RUN mvn clean package -DskipTests

# 使用一个更小的基础镜像来运行应用程序
FROM eclipse-temurin:17-jre

# 设置时区为 UTC+8
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 设置工作目录
WORKDIR /app

# 从构建阶段复制 JAR 文件到容器
COPY --from=builder /app/target/temp-email-mcp-0.0.1-SNAPSHOT.jar app.jar

# Zeabur 会自动注入 PORT 环境变量
EXPOSE 8080

# 必须用 exec 形式启动应用
ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=8080"]
