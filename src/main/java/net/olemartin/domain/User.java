package net.olemartin.domain;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.NodeEntity;

import java.lang.reflect.Type;
import java.security.Principal;

@NodeEntity
public class User implements Principal {

    @GraphId
    private Long id;

    @Index(unique=true)
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
