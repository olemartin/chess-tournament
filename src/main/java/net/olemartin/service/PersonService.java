package net.olemartin.service;

import com.google.common.collect.Lists;
import net.olemartin.business.Person;
import net.olemartin.business.Tournament;
import net.olemartin.repository.PersonRepository;
import net.olemartin.view.PersonView;
import net.olemartin.view.TournamentView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PersonService {

    private final PersonRepository personRepository;
    private final Neo4jTemplate template;

    @Autowired
    public PersonService(PersonRepository personRepository, Neo4jTemplate template) {
        this.personRepository = personRepository;
        this.template = template;
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

    public PersonView getPerson(Long id) {
        Person person = personRepository.findOne(id);

        List<TournamentView> tournamentViews = person.getPlayers().stream()
                .map(player -> new TournamentView(
                        template.fetch(player.getTournament()).getName(),
                        player.getTournamentRank()))
                .collect(Collectors.toList());
        return new PersonView(person.getName(), person.getRating(), tournamentViews);
    }
}
