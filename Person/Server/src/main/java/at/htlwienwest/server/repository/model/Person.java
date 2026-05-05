package at.htlwienwest.server.repository.model;

/**
 *   {
 *     "id": 4,
 *     "firstName": "Samuel",
 *     "lastName": "Fischer",
 *     "email": "samuel.fischer@example.com",
 *     "dateOfBirth": "1992-01-30",
 *     "status": "ACTIVE"
 *   }
 */

public class Person {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String  dateOfBirth;
    private String status;

    public Person() {}

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }
    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}
