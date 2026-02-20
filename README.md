# Phoenix RAG Platform

An enterprise-grade Retrieval-Augmented Generation platform built with Spring AI, pgvector, Apache Kafka, and React.

[![CI Pipeline](https://github.com/Youss2f/phoenix-rag-platform/actions/workflows/ci-pipeline.yml/badge.svg)](https://github.com/Youss2f/phoenix-rag-platform/actions/workflows/ci-pipeline.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

## Architecture

```
[User] → [React Frontend] → [Spring Boot API]
                                    ↓
                        [Document Upload + Tika Extraction]
                                    ↓
                        [Kafka Ingestion Pipeline]
                                    ↓
                        [Document Chunking + Storage]
                                    ↓
                        [PostgreSQL + pgvector]
                                    ↓
                        [Keycloak Auth (OAuth2/OIDC)]
```

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 21, Spring Boot 3.3, Spring AI |
| Vector DB | PostgreSQL 16 + pgvector |
| Messaging | Apache Kafka |
| Auth | Keycloak (OAuth2/OIDC) |
| Frontend | React 18 + TypeScript + Tailwind CSS |
| LLM | Ollama (Llama3) |
| Containerization | Docker Compose |
| CI/CD | GitHub Actions |
| License | MIT |

## Project Status

- [x] Project scaffolding and multi-module Maven setup
- [x] Docker Compose environment (PostgreSQL + pgvector, Kafka, Keycloak, Ollama)
- [x] Keycloak OAuth2/OIDC security integration (JWT resource server)
- [x] Document upload REST API with async processing (returns 202 Accepted)
- [x] Document text extraction via Apache Tika
- [x] Document chunking with configurable size and overlap
- [x] Kafka-based ingestion pipeline (producer/consumer with document-ingestion topic)
- [x] Document status tracking (PENDING → PROCESSING → COMPLETED/FAILED)
- [x] React frontend scaffold with chat UI and dark theme
- [x] GitHub Actions CI pipeline
- [ ] Embedding generation with Spring AI + Ollama
- [ ] Vector similarity search via pgvector
- [ ] Chat service with RAG retrieval and LLM responses
- [ ] Frontend API integration with SSE streaming
- [ ] Document upload UI in frontend
- [ ] Keycloak realm configuration-as-code
- [ ] Multi-tenant support
- [ ] Production deployment on OCI

## Getting Started

### Prerequisites
- Java 21+
- Docker & Docker Compose
- Node.js 18+ (for frontend)

### Run with Docker

```bash
# Clone and configure
git clone https://github.com/Youss2f/phoenix-rag-platform.git
cd phoenix-rag-platform
cp .env.example .env

# Start all services
docker compose up -d --build

# Access points:
# Frontend:       http://localhost:3000
# Backend API:    http://localhost:8081
# Keycloak Admin: http://localhost:8080 (admin/admin)
```

### Run Locally (Development)

```bash
# Start infrastructure only
docker compose up -d postgres zookeeper kafka keycloak ollama

# Run backend
cd backend && ./mvnw spring-boot:run

# Run frontend
cd frontend && npm install && npm run dev
```

## Why This Project

Most enterprise AI deployments need RAG — the ability to ground LLM responses in proprietary company data. This platform demonstrates a production-aligned approach: event-driven document ingestion, semantic vector search, and secure multi-tenant architecture.

## License

MIT License. See [LICENSE](LICENSE) for details.
