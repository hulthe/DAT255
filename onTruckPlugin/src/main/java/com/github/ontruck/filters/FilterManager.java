package com.github.ontruck.filters;

import com.github.ontruck.MopedState;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.List;
import java.util.LinkedList;

public class FilterManager {
	private List<StateFilter> filters = new LinkedList<>();
	private MopedState state;

	public FilterManager(MopedState initialState) {
		this.state = initialState;
	}

	public void processStateEvent(String message) {

		JsonElement json = new JsonParser().parse(message);
		if(!json.isJsonObject()) {
			return;
		}

		JsonObject messageObject = json.getAsJsonObject();
		if(!messageObject.has("type") ||
		   !messageObject.has("value")) {
			return;
		}

		JsonElement typeElement = messageObject.get("type");
		JsonElement valueElement = messageObject.get("value");

		if(!typeElement.isJsonPrimitive() ||
		   !typeElement.isJsonPrimitive()) {
			return;
		}

		String type = typeElement.getAsString();
		String value = valueElement.getAsString();

		if(!"state".equals(type)) {
			return;
		}

		state = MopedState.fromString(value);

		System.out.println(state);
		for (StateFilter filter : filters) {
			filter.setState(state);
		}
	}

	public boolean addFilter(StateFilter filter) {
		return this.filters.add(filter);
	}

	private boolean removeFilter(StateFilter filter) {
		return filters.remove(filter);
	}
}
