package net.olemartin.service;

import com.google.common.collect.Lists;
import net.olemartin.business.Person;
import net.olemartin.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PersonService {

    private final PersonRepository personRepository;

    @Autowired
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }


    public List<Person> getPersons() {
        return Lists.newArrayList(personRepository.getPersons());
    }

    public Person createPerson(Person person) {
        return personRepository.save(person);
    }

    public List<Person> getPersonsNotInTournament(Long tournamentId) {
        return Lists.newArrayList(personRepository.getPersonsNotInTournament(tournamentId));
    }
}
