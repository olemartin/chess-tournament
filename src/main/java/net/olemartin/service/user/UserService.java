package net.olemartin.service.user;

import net.olemartin.domain.User;
import net.olemartin.repository.UserRepository;
import org.neo4j.ogm.cypher.Filters;
import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.Optional;

@Transactional
@Service
public class UserService {

    private final UserRepository userRepository;
    private final Session session;

    @Autowired
    public UserService(UserRepository userRepository, Session session) {
        this.userRepository = userRepository;
        this.session = session;
    }

    public Optional<User> getUser(String username, String password) {

        Collection<User> users = session.loadAll(User.class, new Filters().add("username", username));

        return users.stream()
                .filter(user ->
                        user.getPassword().equals(hashPassword(password, user.getSalt()))
                )
                .findFirst();


    }

    public User createUser(String username, String password, String name) {
        String salt = getSalt();
        User user = new User(username, hashPassword(password, salt), salt, name);
        userRepository.save(user);
        return user;
    }

    private static String getSalt() {
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");

            byte[] salt = new byte[16];
            sr.nextBytes(salt);
            return new String(salt);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(salt.getBytes());
            byte[] bytes = md.digest(password.getBytes());
            StringBuilder builder = new StringBuilder();
            for (byte aByte : bytes) {
                builder.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
