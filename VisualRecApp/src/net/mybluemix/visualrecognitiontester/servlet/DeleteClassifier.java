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
import com.google.gson.JsonObject;

import net.mybluemix.visualrecognitiontester.blmxservices.CloudantClientMgr;
import net.mybluemix.visualrecognitiontester.blmxservices.marcovisualreclibrary.WatsonBinaryClassifier;
import net.mybluemix.visualrecognitiontester.datamodel.Classifier;
import net.mybluemix.visualrecognitiontester.datamodel.Instance;

import net.mybluemix.visualrecognitiontester.blmxservices.marcovisualreclibrary.Utils;

/**
 * This endpoint deletes a custom classifier. Synchronous
 * 
 * @author Marco Dondio
 */
@WebServlet("/DeleteClassifier")
public class DeleteClassifier extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DeleteClassifier() {
		super();

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		System.out.println("[DeleteClassifier doGet()] Function called");
		
		// First, get classifierId
		String classifierId = request.getParameter("classifierId");

		if (classifierId == null) {
			System.out.println("[DeleteClassifier] No classifierId specified");
			JsonObject o = new JsonObject();
			o.addProperty("error", "no classifierId specified");
			response.getWriter().println(o);
			return;

		}

		// Then, retrieve api key
		String apiKey = retrieveInstanceApiKey(classifierId);

		if (apiKey == null) {
			System.out.println("[DeleteClassifier] No classifier found in cloudant");
			JsonObject o = new JsonObject();
			o.addProperty("error", "No classifier found in cloudant");
			response.getWriter().println(o);
			return;
		}
		System.out.println("[DeleteClassifier] Retrieved apiKey: " + apiKey);

		// ------------------------------------------------------
		// XXX potential atomicity problem

		// Update instance in cloudant removing this classifier
		System.out.println("[DeleteClassifier] Updating instance in cloudant...");
		updateInstance("vr_instance_" + apiKey, classifierId);

		// Delete classifier in cloudant
		System.out.println("[DeleteClassifier] Deleting classifier in cloudant...");
		Utils.deleteClassifier(classifierId);
		// ------------------------------------------------------

		// Watson deletion
		System.out.println("[DeleteClassifier] Deleting classifier in Watson...");
		Utils.deleteFromWatson(apiKey, classifierId);

		System.out.println("[DeleteClassifier] Deletion of classifierId " + classifierId + " done!");

		JsonObject o = new JsonObject();
		o.addProperty("message", "Deletion of classifierId " + classifierId + " done!");
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

	// retrieve api key of this classifier
	private String retrieveInstanceApiKey(String classifierId) {

		Database db = CloudantClientMgr.getCloudantDB();

		// Condizione
		String selector = "{\"selector\": {\"_id\" : \"" + classifierId + "\"}}";

		// Limita i campi
		FindByIndexOptions opt = new FindByIndexOptions().fields("_id").fields("instance");

		// execute query
		List<Classifier> classifiers = db.findByIndex(selector, Classifier.class, opt);

		return classifiers.isEmpty() ? null : classifiers.get(0).getApiKey();
	}

	// Update instance, by removing classifier from list
	private void updateInstance(String vr_instanceId, String classifierId) {

		// get db connection
		Database db = CloudantClientMgr.getCloudantDB();

		// Get the instance from db
		Instance i = db.find(Instance.class, vr_instanceId);

		// Remove classifier
		i.removeClassifier(classifierId);

		// now update the remote classifier
		Response responseUpdate = db.update(i);

		System.out.println("[DeleteClassifier] Updated Instance, response: " + responseUpdate);
	}

//	// delete classifier from cloudant
//	private void deleteClassifier(String classifierId) {
//
//		// get db connection
//		Database db = CloudantClientMgr.getCloudantDB();
//
//		// Get the instance from db
//		Classifier c = db.find(Classifier.class, classifierId);
//
//		Response responseDelete = db.remove(c);
//
//		System.out.println("[DeleteClassifier] Deleted classifier, response: " + responseDelete);
//	}
//
//	private void deleteFromWatson(String apiKey, String classifierId) throws IOException {
//
//		WatsonBinaryClassifier classifier = new WatsonBinaryClassifier(apiKey);
//		classifier.setClassifierId(classifierId);
//		classifier.deleteModel();
//	}
}
