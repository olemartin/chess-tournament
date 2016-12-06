package net.olemartin.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile(MonradProfile.PRODUCTION)
public class Production {

    private static String neo4jPath;

    public static void setNeo4jPath(String neo4jPath) {
        Production.neo4jPath = neo4jPath;
    }

    @Bean
    public org.neo4j.ogm.config.Configuration configuration() {
        org.neo4j.ogm.config.Configuration config = new org.neo4j.ogm.config.Configuration();
        config
                .driverConfiguration()
                .setURI("file://" + neo4jPath)
                .setDriverClassName("org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver");
        return config;
    }
}
