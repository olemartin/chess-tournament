package net.olemartin.tools;

import net.olemartin.resources.MatchResource;
import net.olemartin.resources.PlayerResource;
import net.olemartin.resources.TournamentResource;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContext implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
    }

    public static TournamentResource getTournamentResource() {
        return applicationContext.getBean(TournamentResource.class);
    }

    public static MatchResource getMatchResource() {
        return applicationContext.getBean(MatchResource.class);
    }

    public static PlayerResource getPlayerResource() {
        return applicationContext.getBean(PlayerResource.class);
    }
}
