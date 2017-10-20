package com.github.ontruck.states;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.List;
import java.util.LinkedList;

/**
 * The purpose of this class is to collectively set the state of multiple filters.
 */
public class FilterManager {
	private List<StateFilter> filters = new LinkedList<>();
	private MopedState state;

	public FilterManager(MopedState initialState) {
		this.state = initialState;
	}

	public void setState(MopedState state) {
		this.state = state;

		System.out.println(state);
		for (StateFilter filter : filters) {
			filter.setState(state);
		}
	}

	/**
	 * @see <a href="https://github.com/hulthe/DAT255/blob/master/doc/tcp_protocol.md">Protocol Specification</a>
	 * @param message JSON object as a String
	 */
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

		setState(MopedState.fromString(value));
	}

	public boolean addFilter(StateFilter filter) {
		return this.filters.add(filter);
	}

	private boolean removeFilter(StateFilter filter) {
		return filters.remove(filter);
	}
}
