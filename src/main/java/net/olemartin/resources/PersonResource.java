package net.olemartin.resources;

import net.olemartin.business.Person;
import net.olemartin.business.Tournament;
import net.olemartin.service.PersonService;
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


    @Path("/new")
    @POST
    public Person createPerson(Person person) {
        return personService.createPerson(person);
    }
}
