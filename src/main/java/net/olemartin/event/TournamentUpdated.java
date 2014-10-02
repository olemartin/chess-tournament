package net.olemartin.event;

import org.springframework.context.ApplicationEvent;

public class TournamentUpdated extends ApplicationEvent {
    /**
     * Create a new ApplicationEvent.
     *
     * @param source the component that published the event (never {@code null})
     */
    public TournamentUpdated(Object source) {
        super(source);
    }
}
