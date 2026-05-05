package at.htlwienwest.server.service;

import at.htlwienwest.server.repository.interfaces.PersonRepository;
import at.htlwienwest.server.repository.model.Person;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonService {

    private final PersonRepository repository;

    public PersonService(@Qualifier("personJSONRepository") PersonRepository repository) {
        this.repository = repository;
    }

    public List<Person> getAllPersons() {
        return repository.findAll();
    }

    public Optional<Person> getOneById(Long id) {
        return repository.findById(id);
    }

    public Person createPerson(Person person) {
        return repository.save(person);
    }

    public Optional<Person> updatePerson(Long id, Person updatedPerson) {
        return repository.update(id, updatedPerson);
    }

    public boolean deletePerson(Long id) {
        return repository.delete(id);
    }
}
