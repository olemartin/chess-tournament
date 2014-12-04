package net.olemartin.service.person;

import com.google.common.collect.Lists;
import net.olemartin.domain.Person;
import net.olemartin.domain.Rating;
import net.olemartin.domain.view.PersonView;
import net.olemartin.domain.view.PersonInTournamentView;
import net.olemartin.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static net.olemartin.tools.Tools.inReverse;

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

    public List<PersonView> getPersons() {
        return Lists.newArrayList(personRepository.getPersons());
    }

    public Person createPerson(Person person) {
        if (person.getRating() == null) {
            person.setCurrentRating(1200);
        }
        return personRepository.save(person);
    }

    public List<Person> getPersonsNotInTournament(Long tournamentId) {
        return Lists.newArrayList(personRepository.getPersonsNotInTournament(tournamentId));
    }

    public PersonView getPerson(Long id) {
        Person person = personRepository.findOne(id);

        List<PersonInTournamentView> tournamentViews = person.getPlayers().stream()
                .map(player -> new PersonInTournamentView(
                        template.fetch(player.getTournament()).getName(),
                        player.getTournamentRank()))
                .collect(Collectors.toList());

        return new PersonView(
                person.getId(),
                person.getName(),
                person.getCurrentRating(),
                tournamentViews);
    }

    public void deletePerson(Long id) {
        personRepository.delete(id);
    }

    public List getRatings(Long personId) {
        final SimpleDateFormat format = new SimpleDateFormat("dd/MM yyyy");

        List<Rating> ratings = personRepository.getRatings(personId);
        return ratings.stream()
                .map(rating -> Arrays.asList(format.format(rating.getDate()), rating.getRating()))
                .collect(inReverse());
    }


}
