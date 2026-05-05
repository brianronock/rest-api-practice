package at.htlwienwest.server.repository.implementation;

import at.htlwienwest.server.repository.interfaces.PersonRepository;
import at.htlwienwest.server.repository.model.Person;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PersonJDBCRepository implements PersonRepository {
    @Override
    public List<Person> findAll() {
        return List.of();
    }

    @Override
    public Optional<Person> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public Person save(Person person) {
        return null;
    }

    @Override
    public Optional<Person> update(Long id, Person updatedPerson) {
        return Optional.empty();
    }

    @Override
    public boolean delete(Long id) {
        return false;
    }
}
