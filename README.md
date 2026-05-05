# REST Practice Resource Platform

This repository is a growing collection of REST, Spring Boot, Java client, and backend practice projects.

The goal of this repository is not to contain one single finished application. Instead, it is designed as a learning platform where different REST examples can be built step by step, compared, extended, and reused.

Each project focuses on a specific learning goal. Some examples are intentionally simple, while later versions will become more advanced and closer to real-world application structure.

---

## Purpose of this repository

This repository is meant to help practice and understand:

* REST API design
* Spring Boot controllers
* `ResponseEntity`
* CRUD operations
* JSON serialization and deserialization
* Repository interfaces
* Service layers
* File-based persistence
* JDBC persistence
* Java Stream API
* `Optional`
* Java clients using `HttpClient`
* Java clients using Spring `WebClient`
* CLI clients
* JavaFX clients
* Gradual migration from simple examples to more realistic architectures

The focus is on learning concepts clearly instead of jumping directly into large frameworks or complex production setups.

---

## Learning approach

The projects in this repository are built in versions. A topic may start with a very simple implementation, for example:

```text
Controller -> Service -> JSON Repository
```

Later, the same topic can be expanded into:

```text
Controller -> Service -> Repository Interface -> JDBC Repository -> Database
```

Then the client side can be added:

```text
CLI Client -> REST API
```

and later:

```text
JavaFX Client -> REST API
```

This way, each version adds one new layer of complexity. The idea is to understand not only what the final code looks like, but also why each step exists.

## Current projects

### Person
The first project is the **Person** project. It currently contains a Spring Boot server that exposes a REST API for managing persons.

**Current structure:**
```text
Person/
└── Server/
```

**Planned future structure:**
```text
Person/
├── Server/
├── CLI-Client/
└── JavaFX-Client/
```

The Person project is used to practice a single-entity CRUD API.

## Planned project types

This repository may later include several kinds of REST practice projects.

### Single-entity projects
These projects focus on one main model, such as:
* Person
* Order
* Car
* Product
* Customer

These are useful for learning basic CRUD operations. Example:
* `GET    /persons`
* `GET    /persons/{id}`
* `POST   /persons`
* `PUT    /persons/{id}`
* `DELETE /persons/{id}`

### Multi-entity projects
Later projects may include relationships between entities. Examples:
* Customer -> Orders
* Product  -> Categories
* Rental   -> Customer + Items

These projects are useful for learning more realistic backend design.

### Different persistence versions
The same project may appear in different persistence versions. Examples:
* Version 1: In-memory list
* Version 2: JSON file
* Version 3: JDBC
* Version 4: JPA/Hibernate

This makes it easier to compare persistence strategies.

### Different client versions
The same backend may be consumed by different clients. Examples:
* CLI Client using `HttpClient`
* CLI Client using `WebClient`
* JavaFX Client
* Vue Client

This helps show that REST APIs are platform-independent. A REST server can serve many different kinds of clients.

## General architecture pattern

Most projects in this repository follow this basic structure:

**`Controller -> Service -> Repository -> Data Source`**

### Controller
The controller handles HTTP requests and responses. It defines routes such as:
* `GET /persons`
* `POST /persons`
* `PUT /persons/{id}`
* `DELETE /persons/{id}`

The controller should not contain persistence logic.

### Service
The service layer contains application logic. It sits between the controller and repository. In simple examples, the service may only forward calls to the repository. In later versions, it can contain business rules, validation, and coordination between multiple repositories.

### Repository
The repository handles data access. A repository might store data in:
* a JSON file
* a relational database using JDBC
* a database using JPA
* or another external system

The rest of the application should not need to know the storage details.

## Why repository interfaces are used

Many projects use repository interfaces. Example:

```java
public interface PersonRepository {
    List<Person> findAll();
    Optional<Person> findById(Long id);
    Person save(Person person);
    Optional<Person> update(Long id, Person person);
    boolean delete(Long id);
}
```

Then different implementations can be created:
* `PersonJSONRepository`
* `PersonJDBCRepository`
* `PersonJpaRepository`

This makes it possible to switch persistence strategies without rewriting the controller.

## Why this repository contains simple code

Some parts of this repository are intentionally simple. For example, early projects may not include:
* global exception handling
* validation
* authentication
* database transactions
* DTO mapping
* advanced logging
* tests

These topics can be added later as separate learning steps. The goal is to build understanding progressively.

## Recommended learning order

A good learning path through this repository is:

1.  Understand a simple Spring Boot REST controller.
2.  Add a service layer.
3.  Add a repository interface.
4.  Implement JSON-file persistence.
5.  Use `Optional` for not-found cases.
6.  Use `ResponseEntity` for proper HTTP status codes.
7.  Add a CLI client with Java `HttpClient`.
8.  Add a CLI client with Spring `WebClient`.
9.  Add JavaFX as a desktop client.
10. Replace JSON persistence with JDBC.
11. Add validation.
12. Add global exception handling.
13. Add multi-entity relationships.
14. Add tests.

## Current status

This repository is actively growing. The first complete project is:
* `Person/Server`
* `Person/CLI-Client`

More projects and versions will be added over time.

## Contributions

Contributions are welcome. This repository is intended to become a practical learning resource for REST, Spring Boot, Java clients, and backend architecture. Contributions can help expand the examples, improve explanations, add new versions, or introduce new learning paths.

Good contribution ideas include:
* adding new single-entity REST examples
* adding multi-entity examples
* implementing JDBC versions
* adding JavaFX clients
* adding CLI clients
* adding validation examples
* adding global exception handling examples
* improving README explanations
* adding diagrams
* adding tests
* fixing bugs or simplifying code

When contributing, try to keep the educational style of the repository. Code should be understandable, structured, and beginner-friendly where possible. If a contribution adds a more advanced version, it should ideally explain what changed compared to the simpler version.

### Contribution style
Please keep examples focused and readable. A good contribution should answer:
* What concept does this teach?
* What changed compared to the previous version?
* Why was this design chosen?
* How can someone run or test it?

This repository is not only about working code. It is also about making the learning process visible.

## Long-term idea

Over time, this repository should become a small resource platform for learning REST development in Java. The goal is to collect examples that move gradually from simple practice projects to more realistic application structures.

Instead of learning everything at once, each project should make one or two new ideas clear. That makes the repository useful for:
* students
* self-taught developers
* beginners learning Spring Boot
* intermediate developers reviewing architecture patterns
* anyone who wants practical REST examples in Java

## License

*A license will be added later.*
```
