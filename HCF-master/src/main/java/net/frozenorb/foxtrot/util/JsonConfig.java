package net.frozenorb.foxtrot.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.frozenorb.foxtrot.Foxtrot;

import java.io.File;

/**
 * Created by vape on 10/30/2020 at 3:20 PM.
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public final class JsonConfig {

	private static final Gson GSON = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
	private static final JsonParser PARSER = new JsonParser();

	private final File file;
	private JsonObject json;

	public JsonConfig(String fileName) {
		if (!fileName.endsWith(".json")) fileName += ".json";
		this.file = new File(Foxtrot.getInstance().getDataFolder(), fileName);

		try {
			if (!this.file.exists()) {
				this.file.getParentFile().mkdirs();
				this.file.createNewFile();
				FileHelper.writeFile(file, "{}");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		load();
	}

	public JsonObject load() {
		try {
			this.json = (JsonObject) PARSER.parse(FileHelper.readFile(this.file));
		} catch (Exception ex) {
			ex.printStackTrace();
			this.json = new JsonObject();
		}

		return this.json;
	}

	public void save() {
		try {
			FileHelper.writeFile(this.file, GSON.toJson(this.json));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public JsonObject get() {
		if (this.json == null)
			load();

		return this.json;
	}

}