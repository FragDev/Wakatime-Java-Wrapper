package me.fragment.wakawrapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import me.fragment.wakawrapper.request.Request;

public class Summaries {

	private SummariesRange range;

	private int totalSeconds;
	private Map<String, Map<String, JsonElement>> editors = new HashMap<String, Map<String, JsonElement>>();
	private Map<String, Map<String, JsonElement>> languages = new HashMap<String, Map<String, JsonElement>>();
	private Map<String, Map<String, JsonElement>> machines = new HashMap<String, Map<String, JsonElement>>();
	private Map<String, Map<String, JsonElement>> operatingSystems = new HashMap<String, Map<String, JsonElement>>();
	private Map<String, Map<String, JsonElement>> projects = new HashMap<String, Map<String, JsonElement>>();

	public Summaries(SummariesRange range) {
		this.range = range;
	}

	public CompletableFuture<Summaries> fetchData() {
		CompletableFuture<Summaries> future = new CompletableFuture<Summaries>();

		new Thread(() -> {
			try {
				JsonObject data = new Request().getRequest("summaries", new HashMap<String, String>() {
					private static final long serialVersionUID = 1L;

					{
						put("range", range.toString());
					}
				});

				this.totalSeconds = (int) data.get("cummulative_total").getAsJsonObject().get("seconds").getAsDouble();
				data.get("data").getAsJsonArray().get(0).getAsJsonObject().keySet().forEach(key -> {
					JsonObject jsonObj = data.get("data").getAsJsonArray().get(0).getAsJsonObject();

					if (key.equalsIgnoreCase("editors")) {
						this.editors = StreamSupport.stream(jsonObj.get("editors").getAsJsonArray().spliterator(), true).map(json -> json.getAsJsonObject())
								.collect(Collectors.toMap(jsonObject -> ((JsonObject) jsonObject).get("name").getAsString(),
										jsonObject -> jsonObject.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()))));

					} else if (key.equalsIgnoreCase("languages")) {
						this.languages = StreamSupport.stream(jsonObj.get("languages").getAsJsonArray().spliterator(), true).map(json -> json.getAsJsonObject())
								.collect(Collectors.toMap(jsonObject -> ((JsonObject) jsonObject).get("name").getAsString(),
										jsonObject -> jsonObject.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()))));

					} else if (key.equalsIgnoreCase("machines")) {
						this.machines = StreamSupport.stream(jsonObj.get("machines").getAsJsonArray().spliterator(), true).map(json -> json.getAsJsonObject())
								.collect(Collectors.toMap(jsonObject -> ((JsonObject) jsonObject).get("name").getAsString(),
										jsonObject -> jsonObject.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()))));

					} else if (key.equalsIgnoreCase("operating_systems")) {
						this.operatingSystems = StreamSupport.stream(jsonObj.get("operating_systems").getAsJsonArray().spliterator(), true).map(json -> json.getAsJsonObject())
								.collect(Collectors.toMap(jsonObject -> ((JsonObject) jsonObject).get("name").getAsString(),
										jsonObject -> jsonObject.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()))));

					} else if (key.equalsIgnoreCase("projects")) {
						this.projects = StreamSupport.stream(jsonObj.get("projects").getAsJsonArray().spliterator(), true).map(json -> json.getAsJsonObject())
								.collect(Collectors.toMap(jsonObject -> ((JsonObject) jsonObject).get("name").getAsString(),
										jsonObject -> jsonObject.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()))));
					}
				});
			} catch (IOException e) {
				e.printStackTrace();
			}

			future.complete(this);
		}).start();

		return future;
	}

	public SummariesRange getRange() {
		return range;
	}

	public int getTotalSeconds() {
		return totalSeconds;
	}

	public Map<String, Map<String, JsonElement>> getEditors() {
		return editors;
	}

	public Map<String, Map<String, JsonElement>> getLanguages() {
		return languages;
	}

	public Map<String, Map<String, JsonElement>> getMachines() {
		return machines;
	}

	public Map<String, Map<String, JsonElement>> getOperatingSystems() {
		return operatingSystems;
	}

	public Map<String, Map<String, JsonElement>> getProjects() {
		return projects;
	}

	public static enum SummariesRange {
		Today, Yesterday, Last_7_Days, Last_7_Days_from_Yesterday, Last_14_Days, Last_30_Days, This_Week, Last_Week, This_Month, Last_Month;
	}

}
