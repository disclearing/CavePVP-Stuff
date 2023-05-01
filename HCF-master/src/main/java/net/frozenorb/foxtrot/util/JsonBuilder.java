package net.frozenorb.foxtrot.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonBuilder {

	private JsonObject json = new JsonObject();

	public JsonBuilder addProperty(String property, String value) {
		json.addProperty(property, value);
		return this;
	}

	public JsonBuilder addProperty(String property, Number value) {
		json.addProperty(property, value);
		return this;
	}

	public JsonBuilder addProperty(String property, Boolean value) {
		json.addProperty(property, value);
		return this;
	}

	public JsonBuilder addProperty(String property, Character value) {
		json.addProperty(property, value);
		return this;
	}

	public JsonBuilder add(String property, JsonElement element) {
		json.add(property, element);
		return this;
	}

	public JsonObject get() {
		return json;
	}

}
