package at.htlwienwest.server.repository.interfaces;

import at.htlwienwest.server.repository.model.Person;

import java.util.List;
import java.util.Optional;

public interface PersonRepository {
    List<Person> findAll();
    Optional<Person> findById(Long id);
    Person save(Person person);
    Optional<Person> update(Long id, Person updatedPerson);
    boolean delete(Long id);
}
