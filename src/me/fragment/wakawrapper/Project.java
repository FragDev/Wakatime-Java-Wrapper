package me.fragment.wakawrapper;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonObject;

import me.fragment.wakawrapper.request.Request;

public class Project {

	private String name;
	private Date creationDate;
	private String url;
	private int seconds;
	private String description;
	private String fullName;
	private String htmlUrl;

	public Project(String name, Date creationDate, String url) {
		this.name = name;
		this.creationDate = creationDate;
		this.url = url;
	}

	public CompletableFuture<Project> fetchData() {
		CompletableFuture<Project> future = new CompletableFuture<Project>();

		new Thread(() -> {
			try {
				JsonObject data = new Request().getRequest("all_time_since_today", new HashMap<String, String>() {
					private static final long serialVersionUID = 1L;

					{
						put("project", name);
					}
				});
				this.seconds = (int) data.get("data").getAsJsonObject().get("total_seconds").getAsDouble();

				data = new Request().getRequest(this.url, null);
				this.description = data.get("data").getAsJsonObject().get("repository").getAsJsonObject().get("description").getAsString();
				this.fullName = data.get("data").getAsJsonObject().get("repository").getAsJsonObject().get("full_name").getAsString();
				this.htmlUrl = data.get("data").getAsJsonObject().get("repository").getAsJsonObject().get("html_url").getAsString();
			} catch (IOException e) {
				e.printStackTrace();
			}

			future.complete(this);
		}).start();

		return future;
	}

	public String getName() {
		return name;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public String getUrl() {
		return url;
	}

	public int getSeconds() {
		return seconds;
	}

	public boolean asRepository() {
		return this.description != null && this.fullName != null && this.htmlUrl != null;
	}

	public String getDescription() {
		return description;
	}

	public String getFullName() {
		return fullName;
	}

	public String getHtmlUrl() {
		return htmlUrl;
	}

}
