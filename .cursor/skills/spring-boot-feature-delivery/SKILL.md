---
name: spring-boot-feature-delivery
description: Delivers Java Spring Boot backend features using a concise workflow with architecture, API contract, validation, security, and testing gates. Use when the user asks to create or modify Spring Boot backend features in this repository.
---

# Spring Boot Feature Delivery

## Scope

Use this skill when implementing or changing backend features in a Java Spring Boot application.

## Default stack assumptions

- Spring Boot 3.x
- Maven + JUnit 5
- OpenAPI/Swagger enabled
- Layered + hexagonal style (domain isolated from adapters)

If the project differs, adapt to existing conventions first.

## Required workflow (short checklist)

Copy this checklist and keep it updated while working:

```md
Feature Progress:
- [ ] Clarify use case and acceptance criteria
- [ ] Define/update API contract (OpenAPI first)
- [ ] Design domain and application flow (hexagonal boundaries)
- [ ] Implement adapters (web/persistence/integration)
- [ ] Add validation and error mapping
- [ ] Apply security constraints
- [ ] Add/adjust tests (unit + integration)
- [ ] Run verification (tests/lints/build)
```

## Implementation rules

1. Start from contract:
   - Define request/response and status codes before implementation.
   - Keep OpenAPI updated with examples and error responses.

2. Respect hexagonal boundaries:
   - Domain has no framework dependencies where possible.
   - Controllers are thin adapters; no business logic in controllers.
   - Repositories/gateways are adapter-side; domain depends on ports/interfaces.

3. Validation is mandatory:
   - Validate inbound payloads with Bean Validation (`@Valid`, constraints).
   - Return clear client errors for invalid inputs.

4. Security by default:
   - Enforce authentication/authorization for new endpoints.
   - Never hardcode secrets or tokens.
   - Sanitize and constrain inputs; avoid leaking internals in error payloads.

5. Tests are required:
   - Unit tests for domain/service logic.
   - Integration tests for controller + persistence behavior.
   - Include negative-path tests (validation/security/failures).

## Done criteria

A feature is complete only if all are true:

- OpenAPI reflects final behavior.
- New/changed endpoints are validated and secured.
- Tests pass and cover happy + unhappy paths.
- Build passes with no introduced linter/test failures.

## Response style for this skill

When reporting progress or final output:

- Keep it concise and actionable.
- Use:
  - What changed
  - Why
  - How it was verified

## Additional resource

- For a reusable output template, see [templates.md](templates.md).
