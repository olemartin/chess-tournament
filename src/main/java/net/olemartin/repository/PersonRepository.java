package net.olemartin.repository;

import net.olemartin.domain.Person;
import net.olemartin.domain.Rating;
import net.olemartin.domain.view.PersonView;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository  extends GraphRepository<Person> {

    @Query("MATCH (p:Person) -[:RATING]-> (rating)\n" +
            "WITH id(p) as id, p.name as name, rating.rating as rating\n" +
            "ORDER BY rating DESC\n" +
            "RETURN DISTINCT id, name, rating")
    Iterable<PersonView> getPersons();

    @Query("MATCH (p:Person), (t:Tournament) " +
            "WHERE " +
            "id(t) = {id} AND " +
            "NOT (p) -[:IS_PLAYER]-> (:Player) -[:PLAYS_IN]-> (t) " +
            "RETURN p")
    Iterable<Person> getPersonsNotInTournament(@Param("id") Long tournamentId);

    @Query("MATCH (p:Person) -[:RATING|PREVIOUS_RATING *]-> (rating:Rating) " +
            "WHERE id(p) = {personId} " +
            "RETURN rating")
    List<Rating> getRatings(@Param("personId") Long personId);
}
