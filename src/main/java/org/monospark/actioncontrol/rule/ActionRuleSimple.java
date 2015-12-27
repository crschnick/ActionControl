package org.monospark.actioncontrol.rule;

import java.util.Optional;
import java.util.Set;

import org.monospark.actioncontrol.category.Category;
import org.monospark.actioncontrol.rule.filter.ActionFilterTemplate;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;

import com.google.gson.JsonElement;

public abstract class ActionRuleSimple<E extends Event & Cancellable>
        extends ActionRule<E, ActionSettingsSimple<E>> {

    private ActionFilterTemplate filter;

    private ActionSettingsSimple.Deserializer<E> settingsDeserializer;

    protected ActionRuleSimple(String name, Class<E> eventClass) {
        super(name, eventClass);
        filter = createFilter();
        settingsDeserializer = new ActionSettingsSimple.Deserializer<>(this);
    }

    protected abstract ActionFilterTemplate createFilter();

    @Override
    public final void handle(E event) throws Exception {
        Optional<Player> player = event.getCause().first(Player.class);
        if (!player.isPresent()) {
            return;
        }

        Set<Category> categories = Category.getRegistry().getCategories(player.get());
        if (categories.size() == 0) {
            return;
        }

        for (Category c : categories) {
            Optional<ActionSettingsSimple<E>> settings = c.getActionSettings(this);
            if (!settings.isPresent()) {
                continue;
            }

            boolean allowed = settings.get().isAllowed(event);
            if (!allowed) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @Override
    public final ActionSettingsSimple<E> deserializeSettings(JsonElement json) {
        return settingsDeserializer.deserialize(json);
    }

    public final ActionFilterTemplate getFilterTemplate() {
        return filter;
    }
}
