package net.mybluemix.visualrecognitiontester.servlet;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.FindByIndexOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassification;

import net.mybluemix.visualrecognitiontester.backgroundaemons.Job;
import net.mybluemix.visualrecognitiontester.backgroundaemons.JobQueue;
import net.mybluemix.visualrecognitiontester.blmxservices.CloudantClientMgr;
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
@WebServlet("/GetTestResult")
public class GetTestResult extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetTestResult() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		System.out.println("[GetTestResult doGet()] Function called");

		// System.out.println(request.getParameter("array"));

		JsonParser parser = new JsonParser();
		JsonArray tests = parser.parse(request.getParameter("array")).getAsJsonArray();

		System.out.println(tests);
		JsonArray results = new JsonArray();

		// For each test
		for (int i = 0; i < tests.size(); i++) {

			JsonObject test = tests.get(i).getAsJsonObject();

			String testSetId = test.get("test").getAsString();
			String classifierId = test.get("classifier").getAsString();

			// retrieve dataset and classifier object
			Dataset testSet = retrieveTestSet(testSetId);

			// -----------------------
			// debug
			// System.out.println("------------------------");
			// System.out.println(testSet.getId());
			//
			// for(Long img : testSet.getImages().getPositives())
			// System.out.println("positive: " + Long.toUnsignedString(img));
			// for(Long img : testSet.getImages().getNegatives())
			// System.out.println("negative: " + Long.toUnsignedString(img));
			// System.out.println("------------------------");

			// -----------------------

			Classifier classifier = retrieveClassifier(classifierId);

			if (testSet == null || classifier == null)
				continue;

			// Generate the zip files (20 images per file max)
			List<byte[]> zipFiles = generateZipTestSet(testSet);

			// We can run a classification
			JsonObject classificationResult = runClassification(classifier, testSet, zipFiles);

			// Add result to array
			results.add(classificationResult);
		}

		System.out.println(results);

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

	// XXX ottimizza e recupera tutti i testSet indicati con una sola query
	private Dataset retrieveTestSet(String id) {

		// Ricevi get con parametro sub_type
		Database db = CloudantClientMgr.getCloudantDB();

		// Condizione
		String selector = "{\"selector\": {\"_id\" : \"" + id + "\"}}";

		// Limita i campi
		FindByIndexOptions opt = new FindByIndexOptions().fields("_id").fields("label").fields("images");

		// execute query
		List<Dataset> datasets = db.findByIndex(selector, Dataset.class, opt);

		return datasets == null ? null : datasets.get(0);
	}

	// Recupera il classificatore
	private Classifier retrieveClassifier(String id) {
		// Ricevi get con parametro sub_type
		Database db = CloudantClientMgr.getCloudantDB();

		// Condizione
		String selector = "{\"selector\": {\"_id\" : \"" + id + "\"}}";

		// Limita i campi
		FindByIndexOptions opt = new FindByIndexOptions().fields("_id").fields("instance").fields("label")
				.fields("training_size").fields("training_set");

		// execute query
		List<Classifier> classifiers = db.findByIndex(selector, Classifier.class, opt);

		return classifiers == null ? null : classifiers.get(0);
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
		return Utils.buildCompressedStreamBlocks(oo, "images_london", images);
	}

	// This method runs a classification on watson
	private JsonObject runClassification(Classifier classifierJson, Dataset testSet, List<byte[]> zipFiles)
			throws IOException {

		// Istantiate service on BlueMix
		WatsonBinaryClassifier classifier = new WatsonBinaryClassifier(classifierJson.getApiKey());
		classifier.setClassifierId(classifierJson.getID());
		classifier.setLabel(classifierJson.getLabel());

		// Classify all zips against Watson instance
		List<VisualClassification> watsonres;
		try {
			watsonres = classifier.classify(zipFiles, Utils.WATSONMINSCORE);
		} catch (VisualClassifierException e) {

			// if we have an error, we exhausted
			// classification calls
			// TODO rendere più robusto
			System.out.println("[GetTestResult runClassification()] VisualClassifierException: " + e.getMessage());

			// Now we add this classifier to the zombie queue...
			ServletContext ctx = getServletContext();
			@SuppressWarnings("unchecked")
			JobQueue<Job<Classifier>> zombieQueue = (JobQueue<Job<Classifier>>) ctx.getAttribute("zombieQueue");

			zombieQueue.addJob(new Job<Classifier>(classifierJson));

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

			System.out.println("[GetTestResult runClassification()] threshold = " + df.format(stepSize * i) + " tpr = "
					+ df.format(tpr) + " fpr = " + df.format(fpr) + " distance = " + df.format(distance));

			// Update best result
			if (optResult == null || distance < optDistance) {
				optResult = result;
				optDistance = distance;
			}
		}

		// debug
		System.out.println("[GetTestResult runClassification()] selected threshold = "
				+ df.format(optResult.getThreshold()) + " and optDistance = " + df.format(optDistance));

		String id = testSet.getLabel() + " " + testSet.getSize() + " - " + classifierJson.getLabel() + " "
				+ classifierJson.getTrainingSize();

		JsonObject result = buildJsonResult(id, optResult, tprTrace, fprTrace, computeAUCMarco(tprTrace, fprTrace));

		return result;
	}

	private JsonObject buildJsonResult(String id, WatsonBinaryClassificationResult optResult, List<Double> tprTrace,
			List<Double> fprTrace, double auc) {
		JsonObject result = new JsonObject();

		result.addProperty("ID", id);

		result.addProperty("accuracyOpt", optResult.computeMetric(METRIC.accuracy));

		result.add("tprTrace", buildArrayFromList(tprTrace));
		result.add("fprTrace", buildArrayFromList(fprTrace));

		result.addProperty("AUC", auc);

		result.addProperty("trainingSize", optResult.getClassifierTrainingSize());

		result.addProperty("thresholdOpt", optResult.getThreshold());

		result.add("falsePositiveOpt", buildArrayFromList(optResult.getfalsePositives()));

		result.add("falseNegativeOpt", buildArrayFromList(optResult.getfalseNegatives()));

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
	// TODO controllare con andrea
	@SuppressWarnings("unused")
	private double computeAndrea(List<Double> tprTrace, List<Double> fprTrace) {

		double auc = 0.0;

		for (int i = 0; i < (tprTrace.size() - 1); i++)
			auc += ((fprTrace.get(i + 1) - fprTrace.get(i)) * (tprTrace.get(i + 1) + tprTrace.get(i)) / 2);

		// System.out.println("[GetTestResult computeAUCAndrea()] auc = " +
		// auc);

		return auc;
	}

	// https://it.wikipedia.org/wiki/Regola_del_trapezio
	private double computeAUCMarco(List<Double> tprTrace, List<Double> fprTrace) {

		// System.out.println("[GetTestResult computeAUCTrapezio()]");

		double auc = 0.0;

		int n = fprTrace.size();
		double b = fprTrace.get(n - 1);
		double a = fprTrace.get(0);

		for (int k = 0; k < n - 1; k++)
			auc += (tprTrace.get(k) + tprTrace.get(k + 1)) / 2;
		auc *= (b - a) / n;

		System.out.println("[GetTestResult computeAUCMarco()] auc = " + auc);

		return auc;
	}

}
