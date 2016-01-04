package org.monospark.actioncontrol.rule.response;

import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;

public interface ActionResponse {

    <E extends Event & Cancellable> void execute(E event);
}
