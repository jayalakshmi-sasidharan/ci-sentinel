# CI Sentinel 🛡️

A full-stack DevOps observability dashboard that monitors and visualizes CI/CD pipeline runs.
Built with Spring Boot, React, and deployed via the same GitHub Actions pipeline it monitors.

## Tech Stack

**Backend:** Java 21, Spring Boot 3.5, Maven, Lombok  
**Frontend:** React (coming soon)  
**CI/CD:** Jenkins, GitHub Actions  
**Infrastructure:** Docker, Kubernetes, Terraform, Helm (coming in Phase 2)  
**Observability:** Prometheus, Grafana (coming in Phase 3)  

## Project Structure

ci-sentinel/
├── backend/          # Spring Boot REST API
├── frontend/         # React dashboard (coming soon)
├── .github/
│   └── workflows/    # GitHub Actions pipelines
└── Jenkinsfile       # Legacy Jenkins pipeline (migration reference)

## Getting Started

### Prerequisites
- Java 21+
- Maven 3.9+
- Node.js 22+

### Run the backend locally

```bash
cd backend
mvn spring-boot:run
```

Server starts at `http://localhost:8080`

### API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/pipelines` | Returns all pipeline runs |
| GET | `/api/pipelines/health` | Health check |
| GET | `/actuator/health` | Spring Boot actuator health |

### Run Tests

```bash
cd backend
mvn test
```

## CI/CD Pipeline

This project demonstrates a Jenkins to GitHub Actions migration.

**Jenkins** (`Jenkinsfile`) — the original pipeline:
- Checkout → Build → Test → Package

**GitHub Actions** (`.github/workflows/`) — the migrated pipeline:
- Checkout → Build → Test → Package → (Deploy coming soon)

The GitHub Actions pipeline runs automatically on every push to `main` and on every pull request.

## Roadmap

- [x] Phase 1 — Spring Boot backend with REST API
- [x] Phase 1 — JUnit tests
- [x] Phase 1 — Jenkinsfile pipeline
- [x] Phase 1 — GitHub Actions migration
- [ ] Phase 2 — React frontend dashboard
- [ ] Phase 2 — Connect to real GitHub Actions API
- [ ] Phase 2 — Dockerize and deploy to Kubernetes with Helm + Terraform
- [ ] Phase 3 — Prometheus + Grafana observability stack
- [ ] Phase 3 — AI-powered build failure summarization

## Why CI Sentinel?

Most CI/CD dashboards are bolt-ons. CI Sentinel is built by a DevOps engineer
who wanted a single view of pipeline health — and deployed using the exact
infrastructure patterns it's designed to monitor.