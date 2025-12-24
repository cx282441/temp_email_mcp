# 使用带有 JDK 的 OpenJDK 17 镜像作为构建基础
FROM eclipse-temurin:17-jdk AS builder

# 安装 Maven
RUN apt-get update && apt-get install -y maven

# 设置时区为 UTC+8
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

WORKDIR /app

# 拷贝 pom.xml 和源码
COPY pom.xml .
COPY src ./src

# 使用 Maven 构建项目并生成 JAR 包
RUN mvn clean package -DskipTests

# 最终镜像用于运行
FROM eclipse-temurin:17-jre

WORKDIR /app

# 拷贝构建出来的 JAR 包
COPY --from=builder /app/target/web-summary-mcp-0.0.1-SNAPSHOT.jar app.jar

# Zeabur 会自动注入 PORT 环境变量
EXPOSE 8080

# 启动 Java 应用
ENTRYPOINT ["java", "-jar", "app.jar"]
