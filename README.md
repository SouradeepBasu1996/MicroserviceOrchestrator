# MicroserviceOrchestrator

**MicroserviceOrchestrator** is a Spring Boot-based Java API that automates the generation of one or more microservices arranged in a Maven multi-module structure. It streamlines backend development by offering the option to include essential cloud-native components like **Service Discovery (Eureka)** and an **API Gateway (Spring Cloud Gateway)**—all generated dynamically via an integrated AI backend.

---

## 🚀 Features

- Generate single or multiple Java microservices
- Maven multi-module architecture
- Optional support for:
  - **Service Discovery** (using Eureka)
  - **API Gateway** (using Spring Cloud Gateway)
- Uses AI via FastAPI + Ollama for intelligent code generation
- Ideal for developers who want to bootstrap production-grade backend services quickly

---

## 🛠️ Tech Stack

| Layer           | Technology        |
|----------------|-------------------|
| Language        | Java              |
| Framework       | Spring Boot       |
| Build Tool      | Maven             |
| AI Integration  | FastAPI (Python)  |
| Local LLM       | Ollama            |

---

## 📦 Prerequisites

Before setting up the project, ensure the following are installed:

- **Java 17** or higher  
- **Python 3**  
- **[Ollama](https://ollama.com/)** (to serve a local Large Language Model)

---

## 🔧 Getting Started

### 1️⃣ Start Ollama

Make sure Ollama is installed and running locally. From the terminal:

```bash
ollama serve

# Clone the FastAPI backend
git clone https://github.com/SouradeepBasu1996/CodeGenApi
cd CodeGenApi

# Install dependencies
pip install fastapi uvicorn pydantic ollama

# Run the API server
uvicorn main:app --reload

# Clone the orchestrator project
git clone https://github.com/YOUR_USERNAME/MicroserviceOrchestrator
cd MicroserviceOrchestrator
