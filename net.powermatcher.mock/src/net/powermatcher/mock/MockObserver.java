package net.powermatcher.mock;

import net.powermatcher.api.monitoring.AgentObserver;
import net.powermatcher.api.monitoring.events.AgentEvent;

public class MockObserver implements AgentObserver {

    private boolean hasReceivedEvent;

    @Override
    public void update(AgentEvent event) {
        this.hasReceivedEvent = true;
    }

    public boolean hasReceivedEvent() {
        return hasReceivedEvent;
    }
}
