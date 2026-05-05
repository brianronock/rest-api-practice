# Person REST Server

This project is a small Spring Boot REST server for practicing RESTful CRUD operations, layered architecture, repository interfaces, JSON-file persistence, `Optional`, `ResponseEntity`, and Java Stream API usage.

The server exposes a simple `/persons` API and stores the data in a local JSON file. The project is intentionally simple, but it already follows several important backend design ideas that are also used in larger applications.

The goal is not only to make the API work, but to understand how a Spring Boot backend can be structured cleanly.

---

## 1. What this project does

The application manages a list of persons. Each person has the following fields:

```json
{
  "id": 1,
  "firstName": "Anna",
  "lastName": "Müller",
  "email": "anna.mueller@example.com",
  "dateOfBirth": "1995-03-14",
  "status": "ACTIVE"
}
```

The API supports the standard CRUD operations:

| HTTP Method | Endpoint | Meaning |
| :--- | :--- | :--- |
| GET | `/persons` | Get all persons |
| GET | `/persons/{id}` | Get one person by id |
| POST | `/persons` | Create a new person |
| PUT | `/persons/{id}` | Update an existing person |
| DELETE | `/persons/{id}` | Delete a person |

This follows the normal REST pattern where the HTTP verb describes the operation and the URL describes the resource.

## 2. Project structure

The project is split into several packages:

```text
at.htlwienwest.server
├── PersonApplication.java
├── controller
│   └── PersonController.java
├── service
│   └── PersonService.java
└── repository
    ├── interfaces
    │   └── PersonRepository.java
    ├── implementation
    │   ├── PersonJSONRepository.java
    │   └── PersonJDBCRepository.java
    └── model
        └── Person.java
```

This structure separates the application into layers:

`Controller -> Service -> Repository Interface -> Repository Implementation`

Each layer has a different responsibility.

## 3. Application entry point

The application starts in `PersonApplication`:

```java
@SpringBootApplication
public class PersonApplication {

    public static void main(String[] args) {
        SpringApplication.run(PersonApplication.class, args);
    }
}
```

`@SpringBootApplication` tells Spring Boot to start the application, scan the package structure, create beans, and start the embedded web server.

By default, the server runs on:
`http://localhost:8080`

## 4. The model: Person

The `Person` class represents the data object used by the API.

```java
public class Person {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String dateOfBirth;
    private String status;
}
```

The class has:
* A no-argument constructor
* Private fields
* Public getters and setters

The no-argument constructor is important because JSON libraries need a simple way to create an object before filling its fields.

For this version, `dateOfBirth` is stored as a `String` instead of `LocalDate`. This keeps the JSON file handling simple because plain Gson does not handle Java time types automatically without a custom TypeAdapter. The date is still written in ISO format (`yyyy-MM-dd`), for example: `"dateOfBirth": "1995-03-14"`.

Later, this can be changed to `LocalDate` by adding a Gson TypeAdapter or by switching the JSON-file implementation to Jackson.

## 5. The repository interface

The repository contract is defined by `PersonRepository`:

```java
public interface PersonRepository {
    List<Person> findAll();
    Optional<Person> findById(Long id);
    Person save(Person person);
    Optional<Person> update(Long id, Person updatedPerson);
    boolean delete(Long id);
}
```

This interface defines what a person repository must be able to do, but not how the data is stored. That is important because the rest of the application does not need to know whether the data comes from:
* A JSON file
* A database using JDBC
* A future JPA repository
* Another external system

The service layer only depends on the interface. This makes the design flexible.

## 6. JSON repository implementation

The active repository implementation is `PersonJSONRepository`.

```java
@Repository
public class PersonJSONRepository implements PersonRepository {
    // ...
}
```

This class stores and loads persons from a local JSON file:
```java
private static final String FILE_PATH = "person.json";
```

For this to work, the `person.json` file should be located in the project root, beside `pom.xml`, for example:

```text
Server
├── pom.xml
├── person.json
└── src
```

The repository follows a simple file-based persistence pattern:
1. Read the whole JSON file into a `List<Person>`
2. Modify the list in memory
3. Write the whole list back to the JSON file

This is not meant to replace a database. It is a learning-friendly persistence approach before moving to JDBC.

## 7. Reading all persons

The `findAll()` method opens the JSON file with `FileReader`:

```java
try (FileReader reader = new FileReader(FILE_PATH)) {
    Type listType = new TypeToken<List<Person>>() {}.getType();
    List<Person> personList = gson.fromJson(reader, listType);
    // ...
}
```

Gson needs `TypeToken<List<Person>>` because `List<Person>` is a generic type. Due to Java type erasure, `List<Person>.class` does not work. So the project uses: `new TypeToken<List<Person>>() {}.getType()`. This tells Gson that the JSON array should become a `List<Person>`.

If the file is empty, missing, or cannot be read, the method returns an empty list. This keeps the project simple for now.

## 8. Finding one person with Stream API and Optional

The `findById()` method uses the Stream API:

```java
return findAll()
        .stream()
        .filter(p -> Objects.equals(p.getId(), id))
        .findFirst();
```

This replaces the traditional imperative loop:

```java
for (Person person : persons) {
    if (person.getId().equals(id)) {
        return person;
    }
}
```

The stream version works as a pipeline:
* `findAll()` -> load all persons
* `stream()` -> start stream processing
* `filter(...)` -> keep only persons with matching id
* `findFirst()` -> return the first match

The method returns `Optional<Person>` instead of returning `null`. This is useful because a person may not exist. With `Optional`, the caller is forced to handle both cases:
* **Person exists:** `Optional` contains the person
* **Person not found:** `Optional.empty()`

The code uses `Objects.equals(...)` instead of `==` because `Long` is an object type. This avoids problems with object comparison and also handles possible null values more safely.

## 9. Creating a person

The `save()` method creates a new person:

```java
Long nextId = personList
        .stream()
        .mapToLong(Person::getId)
        .max()
        .orElse(0) + 1;
```

This code uses the Stream API to generate the next id. The steps are:
* `stream()` -> process all persons
* `mapToLong(Person::getId)` -> extract only the ids
* `max()` -> find the highest existing id
* `orElse(0)` -> use 0 if the list is empty
* `+ 1` -> create the next id

So if the highest existing id is 10, the next id becomes 11. After that, the repository sets the id, adds the person to the list, writes the list back to the JSON file, and returns the created person:

```java
person.setId(nextId);
personList.add(person);
writeAll(personList);
return person;
```

In a database, this id generation would normally be handled by an auto-increment column. Here, it is done manually because the project uses a JSON file.

## 10. Updating a person

The `update()` method returns `Optional<Person>` because the person may or may not exist. First, the method checks if a person with the given id exists:

```java
Optional<Person> existingPerson = personList.stream()
        .filter(p -> Objects.equals(p.getId(), id))
        .findFirst();
```

If no person exists, the method returns `Optional.empty()`. If the person exists, the path id is written into the updated object:

```java
updatedPerson.setId(id);
```

This is important because the URL should decide which object is updated. For example, `PUT /persons/5` means: update person with id 5. Even if the request body contains another id, the backend uses the path id.

Then a new updated list is created:

```java
List<Person> updatedList = personList.stream()
        .map(p -> Objects.equals(p.getId(), id) ? updatedPerson : p)
        .toList();
```

This means: If this person has the target id, replace it with `updatedPerson`; otherwise, keep the old person. After that, the updated list is written back to the JSON file.

## 11. Deleting a person

The `delete()` method uses:

```java
boolean removed = personList.removeIf(p -> Objects.equals(p.getId(), id));
```

`removeIf(...)` removes all elements that match the condition. Since ids should be unique, at most one person should be removed. The method returns `true` if something was deleted, otherwise `false`.

The controller uses that boolean to decide whether to return `204 No Content` or `404 Not Found`.

## 12. Writing the JSON file

The private method `writeAll()` writes the complete list back into the JSON file:

```java
private void writeAll(List<Person> personList) {
    try (FileWriter writer = new FileWriter(FILE_PATH)) {
        gson.toJson(personList, writer);
    } catch (Exception e) {
        System.out.println("❌ ERROR writeAll(): " + e.getMessage());
    }
}
```

This project does not update individual objects inside the file. Instead, every change rewrites the whole file. That is acceptable for this small learning project. For larger systems, a database is better because it supports:
* Transactions
* Indexing
* Concurrent access
* Constraints
* Relationships
* Safer persistence

## 13. JDBC repository placeholder

The project also contains `PersonJDBCRepository`:

```java
@Repository
public class PersonJDBCRepository implements PersonRepository {
    // ...
}
```

This class is currently only a placeholder. The reason it exists is to show the purpose of the repository interface. Later, the application can switch from JSON persistence to JDBC persistence by implementing the same methods: `findAll()`, `findById()`, `save()`, `update()`, `delete()`.

Because both repositories implement `PersonRepository`, the service layer can stay almost unchanged.

## 14. Choosing the active repository with @Qualifier

The service injects the repository interface:

```java
private final PersonRepository repository;
```

But there are two repository classes (`PersonJSONRepository` and `PersonJDBCRepository`) that both implement the same interface. Therefore, Spring needs to know which one should be injected. This project currently chooses the JSON repository explicitly:

```java
public PersonService(@Qualifier("personJSONRepository") PersonRepository repository) {
    this.repository = repository;
}
```

The bean name is based on the class name (`PersonJSONRepository` -> `personJSONRepository`). Later, this can be changed to `@Qualifier("personJDBCRepository")` when the JDBC implementation is ready. Another option would be to use `@Primary` or Spring profiles, but this project uses `@Qualifier` because it is explicit and easy to understand.

## 15. Service layer

The service class is `PersonService`.

```java
@Service
public class PersonService {
    private final PersonRepository repository;
}
```

The service does not know how the data is stored. It only calls the repository interface.

```java
public List<Person> getAllPersons() {
    return repository.findAll();
}
```

The service layer is useful because it separates application logic from HTTP logic. The controller should not directly read and write files; it should only handle HTTP requests and responses. The repository should not know anything about HTTP status codes. The service sits between both.

The current service is simple, but later it could contain business rules such as:
* Validating status changes
* Checking duplicate emails
* Enforcing update rules
* Deciding whether a person may be deleted
* Coordinating multiple repositories

## 16. Controller layer

The REST API is implemented in `PersonController`.

```java
@RestController
@RequestMapping("/persons")
public class PersonController {
    // ...
}
```

`@RestController` tells Spring that this class handles HTTP requests and returns data directly as JSON. `@RequestMapping("/persons")` sets the base path for all endpoints.

So this method handles `GET /persons`:

```java
@GetMapping
public ResponseEntity<List<Person>> getAll() {
    return ResponseEntity.ok(service.getAllPersons());
}
```

And this method handles `GET /persons/1`:

```java
@GetMapping("/{id}")
public ResponseEntity<Person> getById(@PathVariable Long id) {
    // ...
}
```

This follows the standard Spring REST style, where controller classes define routes using annotations such as `@GetMapping`, `@PostMapping`, `@PutMapping`, and `@DeleteMapping`. Parameters can come from the URL with `@PathVariable` or from the JSON request body with `@RequestBody`.

## 17. Why ResponseEntity is used

The controller does not only return objects. It returns `ResponseEntity<T>`. `ResponseEntity` allows the controller to control:
* The HTTP status code
* The response body
* The response headers

For example:
* `return ResponseEntity.ok(person);` returns `200 OK` with the person as JSON.
* `return ResponseEntity.notFound().build();` returns `404 Not Found` without a response body.
* `return ResponseEntity.noContent().build();` returns `204 No Content` after successful deletion.

This is better than always returning a plain object because REST clients need meaningful HTTP status codes.

## 18. Endpoint behavior

### GET `/persons`
Returns all persons.

```java
@GetMapping
public ResponseEntity<List<Person>> getAll() {
    return ResponseEntity.ok(service.getAllPersons());
}
```

**Response:** `200 OK`
**Body:**
```json
[
  {
    "id": 1,
    "firstName": "Anna",
    "lastName": "Müller",
    "email": "anna.mueller@example.com",
    "dateOfBirth": "1995-03-14",
    "status": "ACTIVE"
  }
]
```
*(An empty list is still returned as `[]` with status `200 OK`)*

### GET `/persons/{id}`
Returns one person by id.

```java
@GetMapping("/{id}")
public ResponseEntity<Person> getById(@PathVariable Long id) {
    return service.getOneById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
}
```

* If the person exists: `200 OK`
* If the person does not exist: `404 Not Found` (works because the service returns an `Optional<Person>`)

### POST `/persons`
Creates a new person.

```java
@PostMapping
public ResponseEntity<Person> create(@RequestBody Person person) {
    Person createdPerson = service.createPerson(person);
    URI location = URI.create("/persons/" + createdPerson.getId());
    return ResponseEntity.created(location).body(createdPerson);
}
```

The client sends a JSON body without an id:
```json
{
  "firstName": "Leonie",
  "lastName": "Leitner",
  "email": "leonie.leitner@example.com",
  "dateOfBirth": "2000-06-17",
  "status": "ACTIVE"
}
```

**Response:** `201 Created`
**Location:** `/persons/{id}`
**Body:**
```json
{
  "id": 11,
  "firstName": "Leonie",
  "lastName": "Leitner",
  "email": "leonie.leitner@example.com",
  "dateOfBirth": "2000-06-17",
  "status": "ACTIVE"
}
```

### PUT `/persons/{id}`
Updates an existing person.

```java
@PutMapping("/{id}")
public ResponseEntity<Person> update(@PathVariable Long id, @RequestBody Person person) {
    return service.updatePerson(id, person)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
}
```

* If the person exists: `200 OK` with the updated person.
* If the person does not exist: `404 Not Found` (The id from the URL is used as the real id).

### DELETE `/persons/{id}`
Deletes one person.

```java
@DeleteMapping("/{id}")
public ResponseEntity<Void> delete(@PathVariable Long id) {
    boolean deleted = service.deletePerson(id);

    if (deleted) {
        return ResponseEntity.noContent().build();
    }

    return ResponseEntity.notFound().build();
}
```

* If deletion succeeds: `204 No Content`
* If no person exists with that id: `404 Not Found`

## 19. Why no global exception handler yet?

This project does not use a global exception handler yet. Instead, the controller handles not-found cases directly using `Optional<Person>` and `ResponseEntity.notFound().build()`. This keeps the code easier to understand for now.

Later, a global exception handler could be added with `@ControllerAdvice`. That would allow the project to centralize error responses for the whole API.

For this version, the focus is on:
* Basic REST endpoints
* Controller-service-repository structure
* `ResponseEntity`
* `Optional`
* JSON file persistence
* Stream API usage

## 20. Example `person.json`

The JSON file should be named `person.json` and placed in the project root.

Example content:
```json
[
  {
    "id": 1,
    "firstName": "Anna",
    "lastName": "Müller",
    "email": "anna.mueller@example.com",
    "dateOfBirth": "1995-03-14",
    "status": "ACTIVE"
  },
  {
    "id": 2,
    "firstName": "David",
    "lastName": "Schneider",
    "email": "david.schneider@example.com",
    "dateOfBirth": "1988-11-02",
    "status": "ACTIVE"
  }
]
```

## 21. Testing the API

The API can be tested with Postman, IntelliJ HTTP Client, VS Code REST Client extension, `curl`, or a custom Java client.

**Get all persons**
```bash
curl http://localhost:8080/persons
```

**Get one person**
```bash
curl http://localhost:8080/persons/1
```

**Create person**
```bash
curl -X POST http://localhost:8080/persons \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Sarah",
    "lastName": "Huber",
    "email": "sarah.huber@example.com",
    "dateOfBirth": "1998-02-12",
    "status": "ACTIVE"
  }'
```

**Update person**
```bash
curl -X PUT http://localhost:8080/persons/1 \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Anna",
    "lastName": "Müller",
    "email": "anna.updated@example.com",
    "dateOfBirth": "1995-03-14",
    "status": "INACTIVE"
  }'
```

**Delete person**
```bash
curl -X DELETE http://localhost:8080/persons/1
```

## 22. Important learning points

This project demonstrates several backend concepts:

* **REST controllers:** Routes are defined with Spring annotations such as `@GetMapping`, `@PostMapping`, `@PutMapping`, and `@DeleteMapping`.
* **Request body mapping:** JSON request bodies are mapped into Java objects with `@RequestBody`.
* **Path variables:** URL values are passed into methods with `@PathVariable`.
* **Proper HTTP responses:** The project uses `ResponseEntity` to return meaningful status codes.
* **Optional for not-found cases:** Instead of returning `null`, the repository and service use `Optional<Person>`.
* **Repository interface:** The application depends on an interface (`PersonRepository`), not directly on one concrete storage implementation.
* **Stream API:** The repository uses streams for finding by id, generating the next id, and updating list entries.
* **JSON persistence:** The first implementation stores data in a JSON file using Gson.
* **Future JDBC support:** A JDBC implementation can later replace the JSON implementation without changing the controller.

## 23. Possible next steps

Good improvements for later:
* Implement `PersonJDBCRepository`.
* Add input validation with `spring-boot-starter-validation`.
* Add a global exception handler with `@ControllerAdvice`.
* Replace `String dateOfBirth` with `LocalDate`.
* Add a Gson `TypeAdapter` or switch JSON persistence to Jackson.
* Add unit tests for service and repository logic.
* Add integration tests for the REST endpoints.
* Add a CLI or JavaFX client using `HttpClient` or `WebClient`.

## 24. Summary

This server is a small but well-structured REST API. It uses Spring Boot for the HTTP layer, a service layer for application logic, a repository interface for persistence abstraction, and a JSON-file repository for simple data storage.

Although the application is small, it already teaches important concepts that also appear in larger backend systems: `Controller -> Service -> Repository -> Data Source`.

The current implementation is intentionally simple, but the architecture makes it easy to extend later with JDBC, validation, exception handling, and more advanced client applications.
```
