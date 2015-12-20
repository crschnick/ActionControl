package org.monospark.actioncontrol.handler.filter;

import java.util.HashSet;
import java.util.Set;

public final class ActionFilterTemplate {

	private Set<ActionFilterOption<?,?>> options;
	
	private ActionFilterTemplate(Set<ActionFilterOption<?,?>> options) {
		this.options = options;
	}

	public Set<ActionFilterOption<?, ?>> getOptions() {
		return options;
	}

	public static Builder builder() {
		return new Builder();
	}
	
	public static final class Builder {
		
		private Set<ActionFilterOption<?,?>> options;
		
		private Builder() {
			options = new HashSet<ActionFilterOption<?,?>>();
		}
		
		public Builder addOption(ActionFilterOption<?, ?> option) {
			options.add(option);
			return this;
		}
		
		public ActionFilterTemplate build() {
			return new ActionFilterTemplate(options);
		}
	}
}
