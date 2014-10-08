package net.olemartin.resources;

import net.olemartin.business.Person;
import net.olemartin.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("person")
@Consumes(MediaType.APPLICATION_JSON)
@Service
@Resource
public class PersonResource {

    private PersonService personService;

    @Autowired
    public PersonResource(PersonService personService) {
        this.personService = personService;
    }


    @Path("/list")
    @GET
    public List<Person> getPersons() {
        return personService.getPersons();
    }
}
