package net.valorhcf.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonConfig {

	private Gson gson;
	private File file;
	private HashMap<String, Object> map;

	public JsonConfig(File file) {
		this(file, new GsonBuilder().create());
	}

	public JsonConfig(File file, Gson gson) {
		this.file = file;
		this.gson = gson;
		this.map = new HashMap<>();
	}

	public JsonConfig load() {
		ensureFileExists();
		loadToMemory();
		return this;
	}

	public HashMap<String, Object> getMap() {
		return map;
	}

	public JsonConfig clear() {
		map = new HashMap<>();
		return this;
	}

	public void ensureFileExists() {
		if (!file.exists()) {
			try {
				file.getParentFile().mkdirs();

				InputStream source = getClass().getResourceAsStream( "/" + file.getName());
				byte[] buffer = new byte[source.available()];

				source.read(buffer);
				source.close();

				OutputStream outStream = new FileOutputStream(file);
				outStream.write(buffer);
				outStream.flush();
				outStream.close();
			} catch (Exception e) {
				throw new RuntimeException("Could not create new file", e);
			}
		}

		if (file.isDirectory()) {
			throw new RuntimeException("Given file is a directory");
		}
	}

	private void loadToMemory() {
		if (!file.exists()) {
			throw new RuntimeException("The config file does not exist");
		}

		try {
			map = gson.fromJson(new FileReader(file), new HashMap<String, Object>().getClass());
		} catch (Exception e) {
			throw new RuntimeException("Failed to load config to memory", e);
		}
	}

	public boolean contains(String path) {
		String[] split = path.split("\\.");

		if (split.length == 1) {
			return map.containsKey(split[0]);
		}

		Object lastElement = null;

		for (int i = 0; i < split.length; i++) {
			String currentPart = split[i];

			if (lastElement == null) {
				lastElement = map.get(currentPart);

				if (!(lastElement instanceof Map)) {
					return false;
				}
			} else {
				if (lastElement instanceof Map) {
					Map nextEntryPoint = (Map) lastElement;

					if (i + 1 == split.length) {
						return nextEntryPoint.containsKey(currentPart);
					} else {
						lastElement = nextEntryPoint.get(currentPart);
					}
				} else {
					return false;
				}
			}
		}

		return false;
	}

	public void set(String path, Object value) {
		String[] split = path.split("\\.");

		if (split.length == 1) {
			map.put(split[0], serializeType(value));
			return;
		}

		Object lastElement = null;

		for (int i = 0; i < split.length; i++) {
			String currentPart = split[i];

			if (lastElement == null) {
				map.computeIfAbsent(currentPart, (k) -> new HashMap<>());

				lastElement = map.get(currentPart);

				if (!(lastElement instanceof Map)) {
					lastElement = new HashMap<>();
					map.put(currentPart, lastElement);
				}
			} else {
				Map map = (Map) lastElement;

				if (i + 1 == split.length) {
					map.put(currentPart, serializeType(value));
				} else {
					map.computeIfAbsent(currentPart, (k) -> new HashMap<>());
					lastElement = map.get(currentPart);
				}
			}
		}
	}

	public Object get(String path) {
		String[] split = path.split("\\.");

		if (split.length == 1) {
			return map.get(split[0]);
		}

		Object lastElement = null;

		for (int i = 0; i < split.length; i++) {
			String currentPart = split[i];

			if (lastElement == null) {
				lastElement = map.get(currentPart);
			} else {
				if (lastElement instanceof Map) {
					lastElement = ((Map) lastElement).get(currentPart);
				} else if (lastElement instanceof List) {
					lastElement = ((List) lastElement).get(Integer.parseInt(currentPart));
				} else if (lastElement.getClass().isPrimitive()) {
					throw new RuntimeException("Cannot continue processing path if last element is primitive or null!");
				}
			}
		}

		return lastElement;
	}

	public <T> T get(String path, Class<T> clazz) {
		return clazz.cast(get(path));
	}

	public <T> T get(String path, T def, Class<T> clazz) {
		T value = clazz.cast(get(path));
		return value == null ? def : value;
	}

	public byte getByte(String path) {
		return get(path, byte.class);
	}

	public byte getByte(String path, byte def) {
		return get(path, def, byte.class);
	}

	public byte[] getByteArray(String path) {
		return get(path, byte[].class);
	}

	public byte[] getByteArray(String path, byte[] def) {
		return get(path, def, byte[].class);
	}

	public boolean getBoolean(String path) {
		return get(path, boolean.class);
	}

	public boolean getBoolean(String path, boolean def) {
		return get(path, def, boolean.class);
	}

	public short getShort(String path) {
		return get(path, short.class);
	}

	public short getShort(String path, short def) {
		return get(path, def, short.class);
	}

	public int getInt(String path) {
		return get(path, Integer.class);
	}

	public int getInt(String path, int def) {
		return get(path, def, Integer.class);
	}

	public double getDouble(String path) {
		return get(path, double.class);
	}

	public double getDouble(String path, double def) {
		return get(path, def, double.class);
	}

	public float getFloat(String path) {
		return get(path, float.class);
	}

	public float getFloat(String path, float def) {
		return get(path, def, float.class);
	}

	public String getString(String path) {
		return get(path, String.class);
	}

	public String getString(String path, String def) {
		return get(path, def, String.class);
	}

	public <T> List<T> getList(String path) {
		Object listObject = get(path);

		if (listObject == null) {
			throw new RuntimeException("No list found at path");
		}

		if (!(listObject instanceof List)) {
			throw new RuntimeException("Object found at path is not a list: " + listObject.getClass().getName());
		}

		return (List<T>) listObject;
	}

	public List<String> getStringList(String path) {
		List<?> list = getList(path);
		List<String> stringList = new ArrayList<>();

		for (Object o : list) {
			stringList.add(o.toString());
		}

		return stringList;
	}

	public void save() {
		file.delete();

		try {
			Files.write(file.toPath(), gson.toJson(map).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
		} catch (IOException e) {
			throw new RuntimeException("Failed to save config", e);
		}
	}

	private Object serializeType(Object value) {
		if (value instanceof JsonElement) {
			return gson.fromJson((JsonElement) value, new HashMap<String, Object>().getClass());
		} else if (value.getClass().isPrimitive() || value instanceof Map || value instanceof ArrayList) {
			return value;
		} else {
			return value.toString();
		}
	}

}
