package org.monospark.actioncontrol.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import org.monospark.actioncontrol.group.Group;
import org.monospark.actioncontrol.handler.ActionHandler;
import org.monospark.actioncontrol.handler.ActionMatcher;
import org.monospark.actioncontrol.handler.ActionSettings;

import java.util.Optional;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

public final class ConfigParser {

	private static final Gson GSON = createGson();
	
	private static Gson createGson() {
		GsonBuilder builder = new GsonBuilder()
				.registerTypeAdapter(ConfigGroup.class, new ConfigGroup.Deserializer())
				.registerTypeAdapter(Config.class, new Config.Deserializer());
		for(ActionHandler<?, ?> handler : ActionHandler.ALL) {
			handler.registerMatcherDeserializers(builder);
		}
		return builder.create();
	}
	
	private ConfigParser() {}
	
	public static Set<Group> parseConfig(Path configFile) throws IOException, ConfigParseException {
		String contents = new String(Files.readAllBytes(configFile));
		Config config = null;
		try {
			config = GSON.fromJson(contents, Config.class);
		} catch (JsonParseException e) {
			throw new ConfigParseException(e);
		}
		return createGroups(config);
	}

	private static Set<Group> createGroups(Config config) throws ConfigParseException {
		validateParents(config);
		
		Set<Group> superGroups = new HashSet<Group>();
		Map<String, ConfigGroup> childGroups = new HashMap<String, ConfigGroup>();
		
		for(Entry<String, ConfigGroup> entry : config.getGroups().entrySet()) {
			if(!entry.getValue().getParent().isPresent()) {
				superGroups.add(new Group(entry.getKey(), entry.getValue().getSettings()));
			} else {
				childGroups.put(entry.getKey(), entry.getValue());
			}
		}
		
		while(childGroups.size() > 0) {
			processGroups(superGroups, childGroups);
		}
		
		return superGroups;
	}
	
	private static void validateParents(Config config) throws ConfigParseException {
		out: for(Entry<String, ConfigGroup> entry1 : config.getGroups().entrySet()) {
			Optional<String> parent = entry1.getValue().getParent();
			if(!parent.isPresent()) {
				continue;
			}
			
			for(Entry<String, ConfigGroup> entry2 : config.getGroups().entrySet()) {
				if(entry1.getKey().equals(entry2.getKey())) {
					continue;
				}

				if(parent.get().equals(entry2.getKey())) {
					continue out;
				}
			}
			
			throw new ConfigParseException("Invalid parent group: \"" + parent.get() + "\"");
		}
	}
	
	private static void processGroups(Set<Group> superGroups, Map<String, ConfigGroup> childGroups)
			throws ConfigParseException {
		for(Entry<String, ConfigGroup> entry : Maps.newHashMap(childGroups).entrySet()) {
			String parentName = entry.getValue().getParent().get();
			Group parent = getGroupByName(parentName, superGroups);
			if(parent != null) {
				Group united = uniteGroups(entry.getKey(), entry.getValue(), parent);
				superGroups.add(united);
				childGroups.remove(entry.getKey());
			}
		}
	}
	
	private static Group getGroupByName(String name, Set<Group> superGroups) {
		for(Group g : superGroups) {
			if(g.getName().equals(name)) {
				return g;
			}
		}
		
		return null;
	}
	
	private static Group uniteGroups(String name, ConfigGroup child, Group parent) throws ConfigParseException {
		Map<ActionHandler<?, ?>, ActionSettings<?>> unitedSettings = Maps.newHashMap();
		for(ActionHandler<?, ?> handler : ActionHandler.ALL) {
			Optional<? extends ActionSettings<?>> united = uniteSettings(handler, name, child, parent);
			if(united.isPresent()) {
				unitedSettings.put(handler, united.get());
			}
		}
		return new Group(name, unitedSettings);
	}
	
	private static <M extends ActionMatcher> Optional<ActionSettings<M>> uniteSettings(ActionHandler<?, M> handler,
			String name, ConfigGroup child, Group parent) throws ConfigParseException {
		Optional<ActionSettings<M>> parentSettings = parent.getActionMatcher(handler);
		@SuppressWarnings("unchecked")
		ActionSettings<M> childSettings = (ActionSettings<M>) child.getSettings().get(handler);
		if(!parentSettings.isPresent()) {
			return Optional.ofNullable(childSettings);
		}
		if(childSettings == null) {
			return Optional.of(parentSettings.get());
		}
		
		if(parentSettings.get().getResponse() != childSettings.getResponse()) {
			throw new ConfigParseException("name can't inherit the " + handler.getName() +
					" settings, since their types are different");
		}
		M united = handler.uniteMatchers(parentSettings.get().getMatcher(), childSettings.getMatcher());
		ActionSettings<M> unitedSettings = new ActionSettings<>(parentSettings.get().getResponse(), united);
		return Optional.of(unitedSettings);
	}
}
