package net.olemartin.repository;

import net.olemartin.business.Person;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository  extends GraphRepository<Person> {

    @Query("MATCH (p:Person) RETURN p ORDER BY p.rating desc")
    public Iterable<Person> getPersons();

}
