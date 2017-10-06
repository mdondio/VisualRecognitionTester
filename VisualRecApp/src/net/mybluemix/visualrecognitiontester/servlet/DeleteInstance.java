package net.mybluemix.visualrecognitiontester.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.FindByIndexOptions;
import com.cloudant.client.api.model.Response;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.mybluemix.visualrecognitiontester.blmxservices.CloudantClientMgr;
import net.mybluemix.visualrecognitiontester.blmxservices.marcovisualreclibrary.WatsonBinaryClassifier;
import net.mybluemix.visualrecognitiontester.blmxservices.marcovisualreclibrary.Utils;
import net.mybluemix.visualrecognitiontester.datamodel.Classifier;
import net.mybluemix.visualrecognitiontester.datamodel.Instance;

/**
 * This endpoint deletes an instance of VisualRecognition. Synchronous
 * This method delete only the DB record, we don't delete physically the instance deployed on the linked private bluemix account
 * Test with (WARNING you are forcing e DELETE action on DB):
 * http://localhost:9080/VisualRecognitionTester/DeleteInstance?instanceId=ID1112344455
 * @author Andrea Bortolossi
 */
@WebServlet("/DeleteInstance")
public class DeleteInstance extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DeleteInstance() {
		super();

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		System.out.println("[DeleteInstance doGet()] Function called");
		
		// First, get instanceId and classifierIds
		JsonParser parser = new JsonParser();
String instanceId = request.getParameter("instance");
String apikey = request.getParameter("instance").substring(12);

		//Note: element 0 is the instance id while elements from 1 to n are classifier ids
		JsonArray classifierIds = parser.parse(request.getParameter("classifier")).getAsJsonArray();

		
		if (instanceId == null) {
			System.out.println("[instanceId] No instanceId specified");
			JsonObject o = new JsonObject();
			o.addProperty("error", "no instanceId specified");
			response.getWriter().println(o);
			return;
		}

		for (int i = 0; i < classifierIds.size(); i++) {
			String classifier = classifierIds.get(i).getAsString();
			System.out.println("[DeleteClassifier] Deleting classifier "+classifier+" in cloudant...");
			Utils.deleteClassifier(classifier);
			System.out.println("[DeleteClassifier] Deleting classifier "+classifier+" in Watson...");
			Utils.deleteFromWatson(apikey, classifier);
		}
		
		Database db = CloudantClientMgr.getCloudantDB();
		Instance instance = db.find(Instance.class, instanceId);
		Response responseDelete = db.remove(instance);

		System.out.println("[DeleteInstance] Deleted instance, response: " + responseDelete);

		JsonObject o = new JsonObject();
		o.addProperty("message", "Deletion of instanceId " + instanceId + " done!");
		response.getWriter().println(o);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
