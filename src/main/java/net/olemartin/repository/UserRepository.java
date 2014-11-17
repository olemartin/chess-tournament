package net.olemartin.repository;

import net.olemartin.business.User;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends GraphRepository<User> {
}
