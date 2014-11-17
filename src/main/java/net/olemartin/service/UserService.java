package net.olemartin.service;

import com.google.common.base.Optional;
import net.olemartin.business.User;
import net.olemartin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Transactional
@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> getUser(String username, String password) {
        User user = userRepository.findBySchemaPropertyValue("username", username);
        String hashedPassword = hashPassword(password, user.getSalt());

        if (user.getPassword().equals(hashedPassword)) {
            return Optional.of(user);
        } else {
            return Optional.absent();
        }
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
