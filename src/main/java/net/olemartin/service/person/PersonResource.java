package net.olemartin.service.person;

import io.dropwizard.auth.Auth;
import net.olemartin.domain.Person;
import net.olemartin.domain.Tournament;
import net.olemartin.domain.User;
import net.olemartin.domain.view.PersonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("person")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Service
@Resource
public class PersonResource {

    private PersonService personService;

    @Autowired
    public PersonResource(PersonService personService) {
        this.personService = personService;
    }


    @Path("/list")
    @POST
    public List<Person> getPersonsNotInTournament(Tournament tournament) {
        return personService.getPersonsNotInTournament(tournament.getId());
    }

    @Path("/list")
    @GET
    public List<Person> getPersons() {
        return personService.getPersons();
    }

    @Path("/")
    @GET
    public List<Person> getAllPersons() {
        return personService.getPersons();
    }

    @Path("/{id}")
    @GET
    public PersonView getPerson(@PathParam("id") Long id) {
        return personService.getPerson(id);
    }

    @Path("/{id}")
    @DELETE
    public String deletePerson(@Auth User user, @PathParam("id") Long id) {
        personService.deletePerson(id);
        return "OK";
    }


    @Path("/new")
    @POST
    public Person createPerson(@Auth User user, Person person) {
        return personService.createPerson(person);
    }
}
