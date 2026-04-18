# Output Template

Use this format for implementation responses:

```md
Feature implemented: <name>

What changed
- <key change 1>
- <key change 2>

Why
- <business/technical reason>

Verification
- [ ] Unit tests: <result>
- [ ] Integration tests: <result>
- [ ] Build/lint: <result>

Notes
- <security/validation/openapi notes>
```

# Quick Design Prompt Template

Before coding, use:

```md
Use case:
Acceptance criteria:
API contract (request/response/errors):
Domain model:
Ports and adapters involved:
Security requirements:
Test plan:
```
