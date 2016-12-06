package net.olemartin.dropwizard;

import io.dropwizard.Configuration;

import javax.validation.constraints.NotNull;

public class MonradConfiguration extends Configuration {

    private String neo4jPath;

    @NotNull
    private String profile;

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getNeo4jPath() {
        return neo4jPath;
    }

    public void setNeo4jPath(String neo4jPath) {
        this.neo4jPath = neo4jPath;
    }
}
