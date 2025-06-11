# 🔐 Authentication Provider – Deployment Guide

This guide provides DevOps engineers with clear instructions on how to build, configure, and deploy the Authentication Provider system using Docker.

---

## 1. Overview

Authentication Provider is a RESTful service that handles:
- Centralized user management via a Web UI and REST API
- User login (username/password)
- JWT token generation/validation/revocation (access & refresh token)
- Social login (Google, Facebook,...)
- In-memory caching using Infinispan
- Access logging

---

## 2. System Requirements

To deploy this application, ensure the following tools are installed:

- Java 17+
- Maven 3.2.5+
- npm 11.3.0+
- Docker
- Docker Compose

---

## 3. Environment Configuration

You can provide the following environment variables to customize the deployment:

### File: `src/main/docker/app.yml`

### Service: `app`

| Key | Description | Default |
|-----|-------------|---------|
| `image` | Docker image name for the application service. | `centralizedauthenticationprovider`|
| `environment._JAVA_OPTIONS` | JVM options such as heap size. | `-Xmx512m -Xms256m`|
| `environment.SPRING_PROFILES_ACTIVE` | Active Spring Boot profiles. | `prod,api-docs`|
| `environment.MANAGEMENT_PROMETHEUS_METRICS_EXPORT_ENABLED` | Enable Prometheus metrics export. | `true`|
| `environment.SPRING_DATASOURCE_URL` | JDBC URL for database connection. | `jdbc:mariadb://mariadb:3306/centralizedauthenticationprovider?useLegacyDatetimeCode=false`|
| `environment.SPRING_LIQUIBASE_URL` | JDBC URL for Liquibase. | (same as `SPRING_DATASOURCE_URL`)|
| `environment.SPRING_DATASOURCE_USERNAME` | Database username. | `administrator`|
| `environment.SPRING_DATASOURCE_PASSWORD` | Database password. | `admin@###2o25`|
| `environment.JAVA_OPTS` | Additional system properties for Java. | `-Djgroups.tcp.address=NON_LOOPBACK -Djava.net.preferIPv4Stack=true`|
| `ports` | Port mapping from host to container. | `8080:8080`|
| `healthcheck.test` | Command to check service health. | `curl -f http://localhost:8080/management/health`|
| `healthcheck.interval` | Interval between health checks. | `5s`|
| `healthcheck.timeout` | Timeout for each health check. | `5s`|
| `healthcheck.retries` | Retry attempts before failing. | `40`|
| `depends_on.mariadb.condition` | Ensures MariaDB is healthy before starting app. | `service_healthy`|

---

### File: `src/main/docker/mariadb.yml`

### Service: `mariadb`
| Key | Description | Default |
|-----|-------------|---------|
| `image` | Docker image for MariaDB. | `mariadb:11.7.2` |
| `volumes[0]` | Mount host config folder to MariaDB config path. | `./config/mariadb:/etc/mariadb/conf.d` |
| `volumes[1]` | Mount folder with SQL initialization scripts. | `./config/init-sql:/docker-entrypoint-initdb.d/` |
| `environment.MARIADB_ALLOW_EMPTY_ROOT_PASSWORD` | Allow root user with empty password. | `yes` |
| `environment.MARIADB_ALLOW_EMPTY_PASSWORD` | Allow users to connect without password. | `yes` |
| `environment.MARIADB_DATABASE` | Name of the database to be created. | `centralizedauthenticationprovider` |
| `ports` | Port mapping (restricted to localhost). | `127.0.0.1:3306:3306` |
| `command` | Start command with charset and timestamp options. | `mariadbd --lower_case_table_names=1 --character_set_server=utf8mb4 --explicit_defaults_for_timestamp` |
| `healthcheck.test` | Health check script for InnoDB initialization. | `['CMD', '/usr/local/bin/healthcheck.sh', '--connect', '--innodb_initialized']` |
| `healthcheck.interval` | Time between health checks. | `5s` |
| `healthcheck.timeout` | Health check timeout. | `5s` |
| `healthcheck.retries` | Number of retries before failing. | `10` |
---

## 4. Docker Deployment
At root folder of the project, you can build and run the Docker container for the Authentication Provider.



### Step 1: Build Java Application

```bash
mvn clean package -DskipTests
```
or package application with tests
```bash
mvn clean package -Dtest=UserExternalServiceImplTest,AuthenticationServiceImplTest
```

### Step 2: Build Docker image

```bash
docker build -t auth-provider .
```

### Step 3: Run Docker Container

```bash
docker compose -f src/main/docker/app.yml up -d 
```

### Step 4: Verify Deployment
```bash
curl -X 'POST' \
  'https://localhost:8080/api/auth/create-token' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "username": "admin",
  "password": "admin"
}' -k
```

