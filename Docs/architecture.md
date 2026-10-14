# System Architecture

## 1. Project Overview

The project aims to build an AI-powered assistant that enables users to query and analyze grocery sales data using natural language.

The system will use a Python-based AI agent, a Java Spring Boot REST API, and a PostgreSQL database.

## 2. Architecture Components

### 2.1. User Interface

The user interface will allow users to submit questions and display the agent's responses.

The frontend technology has not yet been selected.

### 2.2. AI Agent — Python

The AI agent will:
- Interpret questions written in natural language.
- Select from a predefined set of tools corresponding to backend API operations.
- Call the Spring Boot REST API using HTTP and JSON.
- Interpret structured API responses and generate user-friendly answers.
- Handle API errors and requests that cannot be answered with the available tools.

The agent will not have unrestricted direct access to the PostgreSQL database.

### 2.3. Backend — Java and Spring Boot

The backend will expose REST endpoints for accessing and analyzing grocery sales data.

Its responsibilities will include:
- Validating incoming requests.
- Applying business rules.
- Executing database operations through the data-access layer.
- Returning structured JSON responses.
- Handling errors consistently.

### 2.4. Database — PostgreSQL

PostgreSQL will store the grocery sales dataset obtained from Kaggle.

The database schema will be designed after examining the dataset's actual columns, relationships, data types, and constraints.

Database schema changes will be version-controlled.

## 3. Communication

The AI agent will communicate with the backend through HTTP REST endpoints using JSON.

The backend will communicate with PostgreSQL through its data-access layer.

The frontend, when implemented, will communicate with the AI agent through an interface to be defined.

## 4. Initial Design Principles

- Keep responsibilities separate between the agent, backend, and database.
- Use predefined, validated backend operations instead of unrestricted database access.
- Perform filtering, aggregation, and calculations in the database when appropriate.
- Keep secrets and credentials out of version control.
- Version-control code, configuration templates, documentation, and database migrations.
- Make the application easy for all team members to run locally.

## 5. Decisions Still to Be Made

- AI model provider and model.
- Agent framework, if one is needed.
- Frontend technology and communication interface.
- Exact database schema and data-import process.
- Initial API endpoints and response formats.
- Authentication requirements, if any.