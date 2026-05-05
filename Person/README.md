# Person Project

The `Person` project is the first project family in this REST practice repository.

It is used to practice a simple single-entity REST system around one model: `Person`.

The goal is to build the same domain step by step across different applications:

```text
Person/
├── Server/
├── CLI-Client/
└── JavaFX-Client/
```

Each folder is its own Maven project and can be opened, built, and run independently.

---

## Current status

**Currently available:**
```text
Person/
└── Server/
```

**Planned:**
```text
Person/
├── Server/
├── CLI-Client/
└── JavaFX-Client/
```

## What this project teaches

The Person project is meant to show how one REST API can be built and consumed by different types of Java applications.
* The **server** provides the API.
* The **clients** consume the API.

This helps demonstrate an important REST idea:
> **A backend can serve different clients as long as they communicate through HTTP and JSON.**

---

## Folder overview

### Server/
Spring Boot REST API for managing persons. It currently includes:
* REST controller
* service layer
* repository interface
* JSON-file repository
* placeholder JDBC repository
* `ResponseEntity`
* `Optional`
* Stream API usage

*(See the `Server/README.md` for details.)*

### CLI-Client/
Command-line client for consuming the Person REST API. Planned focus:
* Java terminal menu
* CRUD operations from the console
* Java `HttpClient`
* Spring `WebClient`
* comparing both client approaches

*(This folder will have its own README when added.)*

### JavaFX-Client/ *(Planned)*
Desktop client for consuming the Person REST API. Planned focus:
* JavaFX UI
* forms
* tables
* REST calls from a desktop app
* background tasks to avoid freezing the UI

*(This folder will have its own README when added.)*

---

## Maven projects

Each folder inside `Person/` is an independent Maven project. That means each folder can have its own `pom.xml`, `src/`, and `README.md`.

**For example:**
```text
Person/
└── Server/
    ├── pom.xml
    ├── src/
    └── README.md
```

**Later:**
```text
Person/
├── Server/
│   ├── pom.xml
│   └── src/
├── CLI-Client/
│   ├── pom.xml
│   └── src/
└── JavaFX-Client/
    ├── pom.xml
    └── src/
```

*Note: Open the specific folder as a Maven project in your IDE.*

---

## Recommended order

**Start with:**
1. `Person/Server`

**Then add or explore:**
2. `Person/CLI-Client`

**Then later:**
3. `Person/JavaFX-Client`

This keeps the learning path clear:
* Build the API first
* Test it manually
* Consume it from a CLI client
* Consume it from a JavaFX client

---

## Summary

The Person project is a small project family for practicing one complete REST workflow across multiple Java applications.

The detailed explanations belong inside the individual folders. This README only gives the overview.
```
