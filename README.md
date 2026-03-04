# SaaS App Boilerplate Template Generator

This repository provides CLI-powered boilerplate to generate a production-ready Spring Boot SaaS application (hexagonal architecture) with batteries-included backend features, Docker support, OpenAPI docs, and integration-tested modules.


## Prerequisites

- Node.js 18+
- npm

## Install

```bash
npm install
```

## Generate a New App

```bash
npm run template:init -- --name "My Cool App"
```

Optional flags:

- `--output ./generated/my-cool-app` to control the output directory
- `--dry-run` to preview without writing files
- `--force` to overwrite an existing output directory

## Validate Template Drift

```bash
npm run template:check-source-drift
```

## Run CLI Tests

```bash
npm run template:test
```

## Backend Features (Template)

The generated backend is a Spring Boot hexagonal architecture REST API with:

- Authentication (login, JWT, password reset/change, account recovery)
- Two-factor authentication (email/TOTP setup, verify, disable)
- User account management (profile, activation, account lifecycle)
- User subscription management
- Subscription plan catalog and management
- Discount code management and validation
- Contact form handling
- App configuration management
- RBAC (permissions and role groups)
- Admin APIs for users, subscriptions, plans, role groups, discount codes, and app configuration
- Database migrations via Liquibase
- Email notifications/templates
- Rate limiting for auth and API routes
- Metrics/observability with Spring Actuator + Prometheus registry
- File storage abstraction (local/AWS S3 strategy support)

All backend features listed above are covered by integration tests in the template test module.

### API Docs and Health Endpoints

- Swagger UI: `http://localhost:8080/api/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/api/docs`
- Actuator health: `http://localhost:8080/actuator/health`

## Where to Read About the App Itself

For backend setup, how to run the app, architecture, and project details, read:

- [`app/README.md`](./app/README.md)
