# CheckoutProof AI — AI Failure Analysis Service

FastAPI-powered microservice providing intelligent triage for Selenium automated browser tests.

## Features
- **Pydantic Validation**: Strict request/response contracts for test failure inputs.
- **Privacy & PII Sanitization**: Masks emails, credentials, tokens, and system user paths before sending to Gemini.
- **Google Gemini 2.5 Flash**: Delivers rapid root-cause diagnosis, debugging suggestions, and Jira-ready bug reports.
- **Mock / Demo Fallback Mode**: Functions automatically even when no `GEMINI_API_KEY` is present.
- **Interactive UI**: Built-in visual dashboard (`GET /`) and OpenAPI Swagger documentation (`GET /docs`).

## Setup & Running

```cmd
cd ai-service
copy .env.example .env
python -m uvicorn app.main:app --host 127.0.0.1 --port 8000 --reload
```

## Endpoints
- `GET /`: AI Failure Analysis visual dashboard.
- `GET /health`: Health check & API key readiness status.
- `POST /analyze`: Submit test failure for AI triage.
- `GET /docs`: Interactive Swagger documentation.
