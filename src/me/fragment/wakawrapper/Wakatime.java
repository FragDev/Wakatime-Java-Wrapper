package me.fragment.wakawrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonObject;

import me.fragment.wakawrapper.Summaries.SummariesRange;
import me.fragment.wakawrapper.request.Request;

public class Wakatime {

	public static void main(String[] args) {
		new Summaries(SummariesRange.Today).fetchData().thenAccept(summary -> {
			System.out.println(summary.getProjects());
		});
	}

	public static CompletableFuture<List<Project>> getAllProjects() {
		CompletableFuture<List<Project>> future = new CompletableFuture<List<Project>>();

		new Thread(() -> {
			List<Project> projects = new ArrayList<Project>();

			try {
				JsonObject data = new Request().getRequest("projects", null);
				data.get("data").getAsJsonArray().forEach(project -> {
					projects.add(new Project(project.getAsJsonObject().get("name").getAsString(), Utils.getDateFromString(project.getAsJsonObject().get("created_at").getAsString()),
							project.getAsJsonObject().get("url").getAsString()));
				});

				if (data.get("total_pages").getAsInt() > 1) {
					while (data.get("page").getAsInt() < data.get("next_page").getAsInt()) {
						int nextPage = data.get("next_page").getAsInt();

						data = new Request().getRequest("projects", new HashMap<String, String>() {
							private static final long serialVersionUID = 1L;

							{
								put("page", String.valueOf(nextPage));
							}
						});
						data.get("data").getAsJsonArray().forEach(project -> {
							projects.add(new Project(project.getAsJsonObject().get("name").getAsString(),
									Utils.getDateFromString(project.getAsJsonObject().get("created_at").getAsString()), project.getAsJsonObject().get("url").getAsString()));
						});
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			future.complete(projects);
		}).start();

		return future;
	}

}
