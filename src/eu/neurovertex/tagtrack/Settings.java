package eu.neurovertex.tagtrack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Neurovertex
 *         Date: 10/05/2014, 13:29
 */
public class Settings implements Map<String, Object> {
	private static final Type mapTypeToken = new TypeToken<Map<String, Object>>() {}.getType();
	private File file;

	private Map<String, Object> map = new HashMap<>();

	public Settings(String filename) {
		file = new File(filename);
	}

	public void load() throws IOException {
		if (file.exists())
			try (FileReader fr = new FileReader(file); JsonReader reader = new JsonReader(fr)) {

				Gson gson = new Gson();
				map = gson.fromJson(reader, mapTypeToken);
			}
	}

	public void save() throws IOException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		try (PrintWriter out = new PrintWriter(file)) {
			out.print(gson.toJson(map, mapTypeToken));
		}
	}

	@Override
	public Object get(Object key) {
		return map.get(key);
	}

	public String get(String key) {
		return (String) map.get(key);
	}

	public int getInt(String key) {
		return (Integer) map.get(key);
	}

	public boolean containsKey(String key) {
		return map.containsKey(key);
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	@Override
	public Object put(String key, Object value) {
		return map.put(key, value);
	}

	@Override
	public Object remove(Object key) {
		return map.remove(key);
	}

	@Override
	public void putAll(Map<? extends String, ?> m) {
		map.putAll(m);
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public Set<String> keySet() {
		return map.keySet();
	}

	@Override
	public Collection<Object> values() {
		return map.values();
	}

	@Override
	public Set<Entry<String, Object>> entrySet() {
		return map.entrySet();
	}
}
