package net.mybluemix.visualrecognitiontester.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.mybluemix.visualrecognitiontester.telegram.TelegramBot;

/**
 * This class handles all POSTs request sent by github:
 * https://github.com/mdondio/VisualRecognitionTester.git
 * https://developer.github.com/webhooks/
 * 
 * webhook will be: GitHubEventHandler
 * 
 * @author Marco Dondio
 *
 */
@WebServlet("/GitHubEventHandler")
public class GitHubEventHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// Hardcoded: Marco BOT
	// private static final String secretToken =
	// "visualrecognitiontester_token";

	// All these chatID will be notified via telegram
	// io, andrea
	private static final String[] subscribers = { "45847711", "317680622" };

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GitHubEventHandler() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		System.out.println("[GitHubEventHandler] Received request...");

		String event = request.getHeader("X-GitHub-Event");
		String signature = request.getHeader("X-Hub-Signature");

		System.out.println("[GitHubEventHandler] Received event: " + event);
		System.out.println("[GitHubEventHandler] Received signature: " + signature);

		// X-GitHub-Delivery

		// TODO controllo sul token!!!!

		ServletInputStream input = request.getInputStream();

		BufferedReader streamReader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
		StringBuilder responseStrBuilder = new StringBuilder();

		String inputStr;
		while ((inputStr = streamReader.readLine()) != null)
			responseStrBuilder.append(inputStr);

		// update contains JSON received from GitHub!
		JsonObject update = new JsonParser().parse(responseStrBuilder.toString()).getAsJsonObject();
		System.out.println(update.toString());

		if (event.matches("push")) {
			String text = "Push event received! Branch name: " + update.get("ref").getAsString();

			JsonArray commits = update.get("commits").getAsJsonArray();

			for (int i = 0; i < commits.size(); i++) {
				JsonObject c = commits.get(i).getAsJsonObject();
				JsonObject committer = c.get("committer").getAsJsonObject();

				text += "\n" + c.get("id").getAsString().substring(0, 7) + ": " + c.get("message").getAsString()
						+ " (committer: " + committer.get("name").getAsString() + ")";
			}

			// Send all contacts!
			for (String subscriber : subscribers) {
				System.out.println("[GitHubEventHandler] Sending notification to subscriber: " + subscriber);
				TelegramBot.sendMessage(subscriber, text);
			}

		}
		if (event.matches("ping")) { // test
			// test
			response.getWriter().append("ok");
		}

	}

	//////////////////////////////
	/*
	 * { "ref": "refs/heads/changes", "before":
	 * "9049f1265b7d61be4a8904a9a27120d2064dab3b", "after":
	 * "0d1a26e67d8f5eaf1f6ba5c57fc3c7d91ac0fd1c", "created": false, "deleted":
	 * false, "forced": false, "base_ref": null, "compare":
	 * "https://github.com/baxterthehacker/public-repo/compare/9049f1265b7d...0d1a26e67d8f",
	 * "commits": [ { "id": "0d1a26e67d8f5eaf1f6ba5c57fc3c7d91ac0fd1c",
	 * "tree_id": "f9d2a07e9488b91af2641b26b9407fe22a451433", "distinct": true,
	 * "message": "Update README.md", "timestamp": "2015-05-05T19:40:15-04:00",
	 * "url":
	 * "https://github.com/baxterthehacker/public-repo/commit/0d1a26e67d8f5eaf1f6ba5c57fc3c7d91ac0fd1c",
	 * "author": { "name": "baxterthehacker", "email":
	 * "baxterthehacker@users.noreply.github.com", "username": "baxterthehacker"
	 * }, "committer": { "name": "baxterthehacker", "email":
	 * "baxterthehacker@users.noreply.github.com", "username": "baxterthehacker"
	 * }, "added": [
	 * 
	 * ], "removed": [
	 * 
	 * ], "modified": [ "README.md" ] } ], "head_commit": { "id":
	 * "0d1a26e67d8f5eaf1f6ba5c57fc3c7d91ac0fd1c", "tree_id":
	 * "f9d2a07e9488b91af2641b26b9407fe22a451433", "distinct": true, "message":
	 * "Update README.md", "timestamp": "2015-05-05T19:40:15-04:00", "url":
	 * "https://github.com/baxterthehacker/public-repo/commit/0d1a26e67d8f5eaf1f6ba5c57fc3c7d91ac0fd1c",
	 * "author": { "name": "baxterthehacker", "email":
	 * "baxterthehacker@users.noreply.github.com", "username": "baxterthehacker"
	 * }, "committer": { "name": "baxterthehacker", "email":
	 * "baxterthehacker@users.noreply.github.com", "username": "baxterthehacker"
	 * }, "added": [
	 * 
	 * ], "removed": [
	 * 
	 * ], "modified": [ "README.md" ] }, "repository": { "id": 35129377, "name":
	 * "public-repo", "full_name": "baxterthehacker/public-repo", "owner": {
	 * "name": "baxterthehacker", "email":
	 * "baxterthehacker@users.noreply.github.com" }, "private": false,
	 * "html_url": "https://github.com/baxterthehacker/public-repo",
	 * "description": "", "fork": false, "url":
	 * "https://github.com/baxterthehacker/public-repo", "forks_url":
	 * "https://api.github.com/repos/baxterthehacker/public-repo/forks",
	 * "keys_url":
	 * "https://api.github.com/repos/baxterthehacker/public-repo/keys{/key_id}",
	 * "collaborators_url":
	 * "https://api.github.com/repos/baxterthehacker/public-repo/collaborators{/collaborator}",
	 * "teams_url":
	 * "https://api.github.com/repos/baxterthehacker/public-repo/teams",
	 * "hooks_url":
	 * "https://api.github.com/repos/baxterthehacker/public-repo/hooks",
	 * "issue_events_url":
	 * "https://api.github.com/repos/baxterthehacker/public-repo/issues/events{/number}",
	 * "events_url":
	 * "https://api.github.com/repos/baxterthehacker/public-repo/events",
	 * "assignees_url":
	 * "https://api.github.com/repos/baxterthehacker/public-repo/assignees{/user}",
	 * "branches_url":
	 * "https://api.github.com/repos/baxterthehacker/public-repo/branches{/branch}",
	 * "tags_url":
	 * "https://api.github.com/repos/baxterthehacker/public-repo/tags",
	 * "blobs_url":
	 * "https://api.github.com/repos/baxterthehacker/public-repo/git/blobs{/sha}",
	 * "git_tags_url":
	 * "https://api.github.com/repos/baxterthehacker/public-repo/git/tags{/sha}",
	 * "git_refs_url":
	 * "https://api.github.com/repos/baxterthehacker/public-repo/git/refs{/sha}",
	 * "trees_url":
	 * "https://api.github.com/repos/baxterthehacker/public-repo/git/trees{/sha}",
	 * "statuses_url":
	 * "https://api.github.com/repos/baxterthehacker/public-repo/statuses/{sha}",
	 * "languages_url":
	 * "https://api.github.com/repos/baxterthehacker/public-repo/languages",
	 * "stargazers_url":
	 * "https://api.github.com/repos/baxterthehacker/public-repo/stargazers",
	 * "contributors_url":
	 * "https://api.github.com/repos/baxterthehacker/public-repo/contributors",
	 * "subscribers_url":
	 * "https://api.github.com/repos/baxterthehacker/public-repo/subscribers",
	 * "subscription_url":
	 * "https://api.github.com/repos/baxterthehacker/public-repo/subscription",
	 * "commits_url":
	 * "https://api.github.com/repos/baxterthehacker/public-repo/commits{/sha}",
	 * "git_commits_url":
	 * "https://api.github.com/repos/baxterthehacker/public-repo/git/commits{/sha}",
	 * "comments_url":
	 * "https://api.github.com/repos/baxterthehacker/public-repo/comments{/number}",
	 * "issue_comment_url":
	 * "https://api.github.com/repos/baxterthehacker/public-repo/issues/comments{/number}",
	 * "contents_url":
	 * "https://api.github.com/repos/baxterthehacker/public-repo/contents/{+path}",
	 * "compare_url":
	 * "https://api.github.com/repos/baxterthehacker/public-repo/compare/{base}...{head}",
	 * "merges_url":
	 * "https://api.github.com/repos/baxterthehacker/public-repo/merges",
	 * "archive_url":
	 * "https://api.github.com/repos/baxterthehacker/public-repo/{archive_format}{/ref}",
	 * "downloads_url":
	 * "https://api.github.com/repos/baxterthehacker/public-repo/downloads",
	 * "issues_url":
	 * "https://api.github.com/repos/baxterthehacker/public-repo/issues{/number}",
	 * "pulls_url":
	 * "https://api.github.com/repos/baxterthehacker/public-repo/pulls{/number}",
	 * "milestones_url":
	 * "https://api.github.com/repos/baxterthehacker/public-repo/milestones{/number}",
	 * "notifications_url":
	 * "https://api.github.com/repos/baxterthehacker/public-repo/notifications{?since,all,participating}",
	 * "labels_url":
	 * "https://api.github.com/repos/baxterthehacker/public-repo/labels{/name}",
	 * "releases_url":
	 * "https://api.github.com/repos/baxterthehacker/public-repo/releases{/id}",
	 * "created_at": 1430869212, "updated_at": "2015-05-05T23:40:12Z",
	 * "pushed_at": 1430869217, "git_url":
	 * "git://github.com/baxterthehacker/public-repo.git", "ssh_url":
	 * "git@github.com:baxterthehacker/public-repo.git", "clone_url":
	 * "https://github.com/baxterthehacker/public-repo.git", "svn_url":
	 * "https://github.com/baxterthehacker/public-repo", "homepage": null,
	 * "size": 0, "stargazers_count": 0, "watchers_count": 0, "language": null,
	 * "has_issues": true, "has_downloads": true, "has_wiki": true, "has_pages":
	 * true, "forks_count": 0, "mirror_url": null, "open_issues_count": 0,
	 * "forks": 0, "open_issues": 0, "watchers": 0, "default_branch": "master",
	 * "stargazers": 0, "master_branch": "master" }, "pusher": { "name":
	 * "baxterthehacker", "email": "baxterthehacker@users.noreply.github.com" },
	 * "sender": { "login": "baxterthehacker", "id": 6752317, "avatar_url":
	 * "https://avatars.githubusercontent.com/u/6752317?v=3", "gravatar_id": "",
	 * "url": "https://api.github.com/users/baxterthehacker", "html_url":
	 * "https://github.com/baxterthehacker", "followers_url":
	 * "https://api.github.com/users/baxterthehacker/followers",
	 * "following_url":
	 * "https://api.github.com/users/baxterthehacker/following{/other_user}",
	 * "gists_url":
	 * "https://api.github.com/users/baxterthehacker/gists{/gist_id}",
	 * "starred_url":
	 * "https://api.github.com/users/baxterthehacker/starred{/owner}{/repo}",
	 * "subscriptions_url":
	 * "https://api.github.com/users/baxterthehacker/subscriptions",
	 * "organizations_url": "https://api.github.com/users/baxterthehacker/orgs",
	 * "repos_url": "https://api.github.com/users/baxterthehacker/repos",
	 * "events_url":
	 * "https://api.github.com/users/baxterthehacker/events{/privacy}",
	 * "received_events_url":
	 * "https://api.github.com/users/baxterthehacker/received_events", "type":
	 * "User", "site_admin": false } }
	 */

}
