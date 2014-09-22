package net.olemartin;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import net.olemartin.dropwizard.MonradConfiguration;
import net.olemartin.tools.GsonJSONProvider;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.annotation.Resource;
import java.util.Map;


public class Main extends Application<MonradConfiguration> {
    public static void main(String[] args) throws Exception {
        new Main().run(args);
    }

    @Override
    public void initialize(Bootstrap<MonradConfiguration> monradConfigurationBootstrap) {

    }

    @Override
    public void run(MonradConfiguration config, Environment environment) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("/beans.xml");
        Map<String, Object> beans = context.getBeansWithAnnotation(Resource.class);
        for (Object o : beans.values()) {
            environment.jersey().register(o);
        }
        environment.jersey().register(GsonJSONProvider.class);
    }
}