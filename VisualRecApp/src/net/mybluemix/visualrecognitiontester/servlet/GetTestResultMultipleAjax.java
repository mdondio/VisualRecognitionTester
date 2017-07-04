package net.mybluemix.visualrecognitiontester.servlet;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.Response;
import com.cloudant.client.org.lightcouch.NoDocumentException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.ibm.watson.developer_cloud.service.exception.ServiceResponseException;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassification;

import net.mybluemix.visualrecognitiontester.blmxservices.CloudantClientMgr;
import net.mybluemix.visualrecognitiontester.blmxservices.Configs;
import net.mybluemix.visualrecognitiontester.blmxservices.ObjectStorage;
import net.mybluemix.visualrecognitiontester.blmxservices.ObjectStorageClientMgr;
import net.mybluemix.visualrecognitiontester.blmxservices.marcovisualreclibrary.exceptions.VisualClassifierException;
import net.mybluemix.visualrecognitiontester.blmxservices.marcovisualreclibrary.Utils;
import net.mybluemix.visualrecognitiontester.blmxservices.marcovisualreclibrary.WatsonBinaryClassificationResult;
import net.mybluemix.visualrecognitiontester.blmxservices.marcovisualreclibrary.WatsonBinaryClassifier;
import net.mybluemix.visualrecognitiontester.blmxservices.marcovisualreclibrary.WatsonBinaryClassificationResult.METRIC;
import net.mybluemix.visualrecognitiontester.datamodel.Classifier;
import net.mybluemix.visualrecognitiontester.datamodel.Dataset;

/**
 * This servlet will launch a classification on Watson Visual Recognition
 * Service
 */
@WebServlet("/GetTestResultMultipleAjax")
public class GetTestResultMultipleAjax extends HttpServlet {
	private static final long serialVersionUID = 1L;
	// This is the name I'm going to attach to the resultJSON in output!
	// protected TestName tn = new TestName();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetTestResultMultipleAjax() {
		super();
	}

	/**
	 * 
	 * @author Alessandro Pogliaghi { Dondy, creo una classe per rendere
	 *         accessibile il nome del test, non � il massimo ma spero che il
	 *         Dio dell'Informatica sia benevolo con me :) }
	 */
	// protected class TestName {
	//
	// private String name;
	//
	// protected String getTestName(){
	//
	// return name;
	//
	// }
	//
	// protected void setTestname(String name){
	//
	// this.name = name;
	//
	// }
	//
	// }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		System.out.println("[GetTestResultMultipleAjax doGet()] Function called");

		// System.out.println("This is request name: " +
		// request.getParameter("array"));

		JsonParser parser = new JsonParser();
		JsonArray tests = parser.parse(request.getParameter("array")).getAsJsonArray();

		JsonArray results = new JsonArray();
		
		JsonObject JSONObj = tests.get(0).getAsJsonObject();

		String testSetId = JSONObj.get("test").getAsString();
		String classifierId = JSONObj.get("classifier").getAsString();
		String testName = JSONObj.get("name").getAsString();
		String error = null;
		
		// retrieve dataset and classifier object
		Dataset testSet = retrieveDataset(testSetId);

		Classifier classifier = retrieveClassifier(classifierId);
		
		// Generate the zip files (20 images per file max)
		List<byte[]> zipFiles = generateZipTestSet(testSet);
		
		if (testSet == null || classifier == null)
			error = "error";
		
		// We can run a classification
		JsonObject classificationResult = runClassification(testName, classifier, testSet, zipFiles, error);

		// Add result to array
		results.add(classificationResult);

		System.out.println("This is results {doGet}: " + results);

		response.getWriter().append(results.toString());

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doGet(request, response);

	}

	private Dataset retrieveDataset(String id) {

		Database db = CloudantClientMgr.getCloudantDB();

		Dataset d;
		try {
			d = db.find(Dataset.class, id);
		}

		catch (NoDocumentException e) {
			d = null;
		}

		return d;
	}

	// Recupera il classificatore
	private Classifier retrieveClassifier(String id) {

		Database db = CloudantClientMgr.getCloudantDB();

		Classifier c;
		try {
			c = db.find(Classifier.class, id);
		}

		catch (NoDocumentException e) {
			c = null;
		}

		return c;

	}

	// Generate zip files from a dataset
	private List<byte[]> generateZipTestSet(Dataset d) throws IOException {

		// Get instance
		ObjectStorage oo = ObjectStorageClientMgr.getObjectStorage();

		// Get all images
		List<Long> images = new ArrayList<Long>();
		images.addAll(d.getImages().getPositives());
		images.addAll(d.getImages().getNegatives());

		// return list of zips: each zip contains 20 images (max per call)
		return Utils.buildCompressedStreamBlocks(oo, Configs.OO_DEFAULTCONTAINER, images);

	}

	// This method runs a classification on watson
	private JsonObject runClassification(String testName, Classifier classifierJson, Dataset testSet,
			List<byte[]> zipFiles, String error) throws IOException {

		// Istantiate service on BlueMix
		String classifierId = classifierJson.getID();
		WatsonBinaryClassifier classifier = new WatsonBinaryClassifier(classifierJson.getApiKey());
		classifier.setClassifierId(classifierId);
		classifier.setLabel(classifierJson.getLabel());

		// Classify all zips against Watson instance
		List<VisualClassification> watsonres;
		try {
			watsonres = classifier.classify(zipFiles, Utils.WATSONMINSCORE);
		} catch (VisualClassifierException | ServiceResponseException e) {

			// if we have an error, we exhausted
			// classification calls
			// TODO rendere pi� robusto
			System.out.println(
					"[GetTestResultMultipleAjax runClassification()] VisualClassifierException: " + e.getMessage());

			System.out.println("[GetTestResultMultipleAjax runClassification()] Setting classifierId: " + classifierId
					+ " as zombie!");
			setAsZombie(classifierId);

			// return an empty object to client
			// TODO definire un formato di errore!
			return new JsonObject();
		}
		// Compute results and metrics
		// TODO passarli come parametri aggiuntivi alla simulazione?
		// double minThreshold = 0.05;
		// double maxThreshold = 0.6;
		double stepSize = 0.05; // era 0.05

		int step = (int) (1.0 / stepSize);

		DecimalFormat df = new DecimalFormat("#.###");
		/////////////////////////////////////////////////////////////////////////////

		// Compute optimal threshold = pair (fprs, tprs)
		// fpr is on Y axis, tpr on X axis
		// and return the associated WatsonBinaryClassificationResult.
		// Optimal result: the one with minimum distance from vertex
		WatsonBinaryClassificationResult optResult = null;
		double optDistance = 1;

		// Traces are needed to plot result
		List<Double> tprTrace = new ArrayList<Double>();
		List<Double> fprTrace = new ArrayList<Double>();

		// for each threshold step...
		for (int i = step; i >= 0; i--) {

			WatsonBinaryClassificationResult result = new WatsonBinaryClassificationResult(classifierJson, testSet,
					watsonres, stepSize * i);

			double tpr = result.computeMetric(METRIC.tpr);
			double fpr = result.computeMetric(METRIC.fpr);

			// Save trace for later
			tprTrace.add(tpr);
			fprTrace.add(fpr);

			// Compute distance.. perfect classificator -> tpr = 1, fpr = 0
			double distance = Math.sqrt(Math.pow(fpr, 2) + Math.pow(1 - tpr, 2));

			System.out.println("[GetTestResultMultipleAjax runClassification()] threshold = " + df.format(stepSize * i)
					+ " tpr = " + df.format(tpr) + " fpr = " + df.format(fpr) + " distance = " + df.format(distance));

			// Update best result
			if (optResult == null || distance < optDistance) {
				optResult = result;
				optDistance = distance;
			}
		}

		// debug
		System.out.println("[GetTestResultMultipleAjax runClassification()] selected threshold = "
				+ df.format(optResult.getThreshold()) + " and optDistance = " + df.format(optDistance));

		String id = testSet.getLabel() + " " + testSet.getSize() + " - " + classifierJson.getLabel() + " "
				+ classifierJson.getTrainingSize();

		System.out.println("Sto usando come testName: " + testName);
		
		if( error == null )
			error = "success";

		JsonObject result = buildJsonResult(id, testName, optResult, tprTrace, fprTrace, computeAuc(tprTrace, fprTrace), error);

		return result;

	}

	private JsonObject buildJsonResult(String id, String testname, WatsonBinaryClassificationResult optResult,
			List<Double> tprTrace, List<Double> fprTrace, double auc, String error) {
		JsonObject result = new JsonObject();

		result.addProperty("ID", id);

		result.addProperty("name", testname);

		result.addProperty("accuracyOpt", optResult.computeMetric(METRIC.accuracy));

		result.add("tprTrace", buildArrayFromList(tprTrace));
		result.add("fprTrace", buildArrayFromList(fprTrace));

		result.addProperty("AUC", auc);

		result.addProperty("trainingSize", optResult.getClassifierTrainingSize());

		result.addProperty("thresholdOpt", optResult.getThreshold());

		result.add("falsePositiveOpt", buildArrayFromList(optResult.getfalsePositives()));

		result.add("falseNegativeOpt", buildArrayFromList(optResult.getfalseNegatives()));

		result.add("histogramPositive", buildArrayFromList(optResult.getHistogramPositive()));
		result.add("histogramNegative", buildArrayFromList(optResult.getHistogramNegative()));

		result.addProperty("notification", error);

		return result;
		
	}

	// Method to build array from list
	// Nota: per gestire correttamente unsigned long devo
	// registrare un adapter custom
	private <T> JsonArray buildArrayFromList(List<T> list) {

		// If list is double, treat as unsigned string
		Gson gson = new GsonBuilder().registerTypeAdapter(Long.class, new JsonSerializer<Long>() {
			public JsonElement serialize(Long l, Type t, JsonSerializationContext arg2) {
				return new Gson().toJsonTree(Long.toUnsignedString(l));
			}
		}).create();

		return gson.toJsonTree(list).getAsJsonArray();
	}

	// Computes AUC from a given tprTrace and fprTrace
	private double computeAuc(List<Double> tprTrace, List<Double> fprTrace) {

		double auc = 0.0;

		for (int i = 0; i < (tprTrace.size() - 1); i++)
			auc += ((fprTrace.get(i + 1) - fprTrace.get(i)) * (tprTrace.get(i + 1) + tprTrace.get(i)) / 2);

		// System.out.println("[GetTestResultMultipleAjax computeAUCAndrea()]
		// auc = " +
		// auc);

		return auc;
	}

	private void setAsZombie(String classifierId) {

		// get db connection
		Database db = CloudantClientMgr.getCloudantDB();

		// Get classifier
		Classifier c = db.find(Classifier.class, classifierId);

		// Update classifier
		c.setStatus("zombie");
		c.setZombieSince(new Date());

		// now update the remote classifier
		Response responseUpdate = db.update(c);

		System.out.println("[ZombieDaemon] Updated cloudant db, response: " + responseUpdate);

	}
}
