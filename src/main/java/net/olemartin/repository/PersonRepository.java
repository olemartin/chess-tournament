package net.olemartin.repository;

import net.olemartin.domain.Person;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository  extends GraphRepository<Person> {

    @Query("MATCH (p:Person) RETURN p ORDER BY p.rating desc")
    Iterable<Person> getPersons();


    @Query("MATCH (p:Person), (t:Tournament)\n" +
            "WHERE \n" +
            "id(t) = {id} AND \n" +
            "NOT p -[:IS_PLAYER]-> (:Player) -[:PLAYS_IN]-> (t) \n" +
            "RETURN p")
    Iterable<Person> getPersonsNotInTournament(@Param("id") Long tournamentId);
}
