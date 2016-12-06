package net.olemartin.spring;

import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@org.springframework.context.annotation.Configuration
@ComponentScan(basePackages = {
        "net.olemartin.service",
        "net.olemartin.push",
        "net.olemartin.domain",
        "net.olemartin.tools",
        "net.olemartin.repository",
        "net.olemartin.spring"
})
@EnableNeo4jRepositories(basePackages = "net.olemartin.repository")
@EnableTransactionManagement
public class Bootstrap {

    @Autowired
    private org.neo4j.ogm.config.Configuration configuration;

    @Bean
    public SessionFactory sessionFactory() {
        return new SessionFactory(configuration, "net.olemartin.domain");
    }

    @Bean
    public Neo4jTransactionManager transactionManager() {
        return new Neo4jTransactionManager(sessionFactory());
    }
}
