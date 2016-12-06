package net.olemartin;

import com.google.common.cache.CacheBuilderSpec;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import net.olemartin.domain.User;
import net.olemartin.dropwizard.MonradConfiguration;
import net.olemartin.push.ChangeNotification;
import net.olemartin.service.user.ChessAuthenticator;
import net.olemartin.service.user.UserService;
import net.olemartin.spring.MonradProfile;
import net.olemartin.spring.Production;
import net.olemartin.tools.GsonJSONProvider;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.AbstractEnvironment;

import javax.annotation.Resource;
import javax.servlet.ServletRegistration;
import java.util.Map;


public class Main extends Application<MonradConfiguration> {
    public static void main(String[] args) throws Exception {
        //new Main().run(args);
        System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, "production");
        new Main().run(new String[]{"server", "monrad.yaml"});
    }

    @Override
    public void initialize(Bootstrap<MonradConfiguration> bootstrap) {
        CacheBuilderSpec.disableCaching();
    }

    @Override
    public void run(MonradConfiguration config, Environment environment) throws Exception {
        System.setProperty("spring.profiles.active", config.getProfile());
        if (config.getProfile().equals(MonradProfile.PRODUCTION)) {
            Production.setNeo4jPath(config.getNeo4jPath());
        }
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(net.olemartin.spring.Bootstrap.class);
        Map<String, Object> beans = context.getBeansWithAnnotation(Resource.class);
        context.getBean(UserService.class).createUser("olemartin", "okki1234", "Ole-Martin");
        context.getBean(UserService.class).createUser("selbekk", "scratcharoo", "Kristoffer");
        for (Object o : beans.values()) {
            environment.jersey().register(o);
        }
        environment.jersey().register(GsonJSONProvider.class);
        environment.jersey().setUrlPattern("/rest/*");
        environment.jersey().register(new AuthDynamicFeature(
                new BasicCredentialAuthFilter.Builder<User>()
                        .setAuthenticator(context.getBean(ChessAuthenticator.class))
                        .setRealm("SUPER SECRET STUFF")
                        .buildAuthFilter()));

        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));


        ServletRegistration.Dynamic websocket = environment.servlets().addServlet("websocket", context.getBean(ChangeNotification.class));
        websocket.setAsyncSupported(true);
        websocket.addMapping("/push/*");
    }
}