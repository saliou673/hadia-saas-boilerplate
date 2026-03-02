
# Maitrise TCF Project

## Description

A modern web-based platform for managing and selling electronic stamps securely and efficiently.

## Technologies Used

- **Backend**: Spring boot 3+
- **Frontend**: NextJS 16+
- **Database**: PostgreSQL


## Development Setup

1. Install java using [sdkman](https://sdkman.io/install)
   - Install [sdkman](https://sdkman.io/install)
   - Install maven using `sdk install maven 3.9.9`
   - Install java using `sdk install java 21.0.8-tem`
2. Install node using [nvm](https://github.com/nvm-sh/nvm)
3. Install pnpm using [pnpm](https://pnpm.io/installation) 

## Running the project

### 1. Clone the repository

```bash
git clone git@github.com:maitrice-tcf/maitrisetcf.git
cd maitrisetcf
```

##### 2. Create the backend environment variables

Copy the `.env.example` in to `.env` in `backend/etimbre-api` module fill it.

##### 3. Docker container
- Start the database container
```bash
    docker compose -f docker/docker-compose.yml up -d
```
- Stop the database container

```bash
    docker compose -f docker/docker-compose.yml down
```

##### 4. Installer les dépendances backend

```bash
cd backend/maitrisetcf-restapi
mvn clean install
```

## Swagger and actuator endpoints
- Swagger UI: http://localhost:8080/api/swagger-ui/index.html
- Swagger JSON: http://localhost:8080/api/docs
- Actuator health: http://localhost:8080/actuator/health

```bash
cd frontend
pnpm install
```
##### 5. Install frontend dependencies:

## Documentation

### 1. [Coding convention](./docs/coding-convention.md)
### 2. [Architecture](./docs/architecture.md)
### 3. [How to configure SMTP](./docs/how-to-configure-smtp.md)
### 4. [Permissions & Role Groups](./docs/permissions.md)

## License

This project is private and the property of **Maitrise TCF**.

