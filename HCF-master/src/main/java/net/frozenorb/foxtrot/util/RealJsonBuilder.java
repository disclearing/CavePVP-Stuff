package net.frozenorb.foxtrot.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class RealJsonBuilder {

	private JsonObject json = new JsonObject();

	public RealJsonBuilder addProperty(String property, String value) {
		json.addProperty(property, value);
		return this;
	}

	public RealJsonBuilder addProperty(String property, Number value) {
		json.addProperty(property, value);
		return this;
	}

	public RealJsonBuilder addProperty(String property, Boolean value) {
		json.addProperty(property, value);
		return this;
	}

	public RealJsonBuilder addProperty(String property, Character value) {
		json.addProperty(property, value);
		return this;
	}

	public RealJsonBuilder add(String property, JsonElement element) {
		json.add(property, element);
		return this;
	}

	public JsonObject get() {
		return json;
	}

}
