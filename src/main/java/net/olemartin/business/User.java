package net.olemartin.business;

import com.google.gson.*;
import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;

import java.lang.reflect.Type;

@NodeEntity
public class User {

    @GraphId
    private Long id;

    @Indexed(unique = true)
    private String username;

    private String password;
    
    private String salt;
    private String name;

    public User(String username, String password, String salt, String name) {

        this.username = username;
        this.password = password;
        this.salt = salt;
        this.name = name;
    }

    private User(){}

    public String getSalt() {
        return salt;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public static class UserSerializer implements JsonSerializer<User> {

        @Override
        public JsonElement serialize(User src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject root = new JsonObject();
            root.addProperty("id", src.id);
            root.addProperty("name", src.name);
            root.addProperty("username", src.username);
            return root;
        }
    }
}
