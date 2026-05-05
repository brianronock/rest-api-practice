package at.htlwienwest.server.repository.implementation;

import at.htlwienwest.server.repository.interfaces.PersonRepository;
import at.htlwienwest.server.repository.model.Person;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Repository;

import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class PersonJSONRepository implements PersonRepository {

    private static final String FILE_PATH = "person.json";
    private final Gson gson = new Gson();

    @Override
    public List<Person> findAll() {
        try (FileReader reader = new FileReader(FILE_PATH)) {
            Type listType = new TypeToken<List<Person>>() {}.getType();
            List<Person> personList = gson.fromJson(reader, listType);

            if (personList == null) {
                System.out.println("❌ ERROR: No persons found!");
                return new ArrayList<>();
            }
            return personList;

        } catch (Exception e) {
            System.out.println("❌ ERROR findAll(): " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public Optional<Person> findById(Long id) {
        return findAll()
                .stream()
                .filter(p -> Objects.equals(p.getId(), id))
                .findFirst();
    }

    @Override
    public Person save(Person person) {
        List<Person> personList = findAll();
        Long nextId = personList
                .stream()
                .mapToLong(Person::getId)
                .max()
                .orElse(0) + 1;
        person.setId(nextId);
        personList.add(person);
        writeAll(personList);
        return person;
    }

    @Override
    public Optional<Person> update(Long id, Person updatedPerson) {
        List<Person> personList = findAll();
        Optional<Person> existingPerson = personList.stream()
                .filter(p -> Objects.equals(p.getId(), id))
                .findFirst();
        if (existingPerson.isEmpty()) {
            return Optional.empty();
        }

        updatedPerson.setId(id);
        List<Person> updatedList = personList.stream()
                .map(p -> Objects.equals(p.getId(), id) ? updatedPerson : p)
                .toList();
        writeAll(updatedList);
        return Optional.of(updatedPerson);
    }

    @Override
    public boolean delete(Long id) {
        List<Person> personList = findAll();
        boolean removed = personList.removeIf(p -> Objects.equals(p.getId(), id));

        if (removed) {
            writeAll(personList);
        }
        return removed;
    }

    private void writeAll(List<Person> personList) {
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(personList, writer);
        } catch (Exception e) {
            System.out.println("❌ ERROR writeAll(): " + e.getMessage());
        }
    }
}
