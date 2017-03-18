package net.mybluemix.visualrecognitiontester.servlet;

//import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
//import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
//import com.google.gson.JsonSyntaxException;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassification;

import net.mybluemix.visualrecognitiontester.blmxservices.CloudantClientMgr;
import net.mybluemix.visualrecognitiontester.blmxservices.ObjectStorage;
import net.mybluemix.visualrecognitiontester.blmxservices.ObjectStorageClientMgr;
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

		// parse request: a list of testSet - classifier
		HashMap<String, String> pairs = parseRequest(request);

		// Create result object
		// JsonObject o = new JsonObject();
		JsonArray results = new JsonArray();

		// For each pair..
		for (String testSetId : pairs.keySet()) {

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

			Classifier classifier = retrieveClassifier(pairs.get(testSetId));

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

		// CORRETTO DA USARE
		response.getWriter().append(results.toString());

		// XXX togliere
		// response.getWriter().append(testResult());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	// This function translates request into an useful data structure
	private HashMap<String, String> parseRequest(HttpServletRequest request) {

		String arrayArg = request.getQueryString();
		System.out.println(arrayArg);

		int i = arrayArg.indexOf("%2C") + 3;

		arrayArg = arrayArg.substring(i, arrayArg.length());

		String testSetID = arrayArg.split("%2C")[0];
		String classifierID = arrayArg.split("%2C")[1];

		System.out.println(testSetID + " - " + classifierID);

		////////////////////////////////////////////////////////
		// test purpose XXX
		// qui id del dataset - classifier ID / altro per usare uno specifico
		//////////////////////////////////////////////////////// classificatore
		HashMap<String, String> pairs = new HashMap<String, String>();
		// pairs.put("watch_test01", "watch_classifier_1559642317");
		pairs.put(testSetID, classifierID);

		// http://localhost:9080/VisualRecognitionTester/show.html?
		// arr=a,b,yyxxxxxxx,yyxxxxxxx,yyxxxxxxx,xxxxxxxxx

		// diviso 3 = numero di triplette

		////////////////////////////////////////////////////////
		return pairs;

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
		
		System.out.println("-------------------------------------------");

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
		List<VisualClassification> watsonres = classifier.classify(zipFiles, Utils.WATSONMINSCORE);

		// Compute results and metrics
		// TODO passarli come parametri aggiuntivi alla simulazione?
		double minThreshold = 0.05;
		double maxThreshold = 0.6;
		double step = 0.05; // era 0.05

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

		// for (double threshold = minThreshold; threshold <= maxThreshold;
		// threshold += step) {
		for (double threshold = maxThreshold; threshold >= minThreshold; threshold -= step) {

			WatsonBinaryClassificationResult result = new WatsonBinaryClassificationResult(classifierJson, testSet,
					watsonres, threshold);

			double tpr = result.computeMetric(METRIC.tpr);
			double fpr = result.computeMetric(METRIC.fpr);

			// Save trace for later
			tprTrace.add(tpr);
			fprTrace.add(fpr);

			// Compute distance.. perfect classificator -> tpr = 1, fpr = 0
			// XXX checkme
			// double distance = Math.sqrt(Math.pow(1 - tpr, 2) + Math.pow(1 -
			// fpr, 2));
			double distance = Math.sqrt(Math.pow(fpr, 2) + Math.pow(1 - tpr, 2));

			System.out.println("[GetTestResult runClassification()] threshold = " + df.format(threshold) + " tpr = "
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

		// Build JSON with optimal result
		// JsonObject result = buildJsonResult(optResult, tprTrace, fprTrace,
		// computeAUC(tprTrace, fprTrace));

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

	// to quickly test integration
	// // XXX to be removed
	// private String testResult() throws JsonIOException, JsonSyntaxException,
	// FileNotFoundException {
	//
	// String s = "[{\"ID\":\"test1\",\"accuracy\": 0.87,\"fpr\":[0, 0.2, 0.4,
	// 1],\"tpr\":[0, 0.4, 0.7, 1],\"AUC\":0.67,\"trainingSize\": 50,
	// \"threshold\":0.2,\"falsepositive\":[\"img/img_nature_wide.jpg\",\"img/img_fjords_wide.jpg\"],\"falsenegative\":[\"img/img_mountains_wide.jpg\",\"img/img_lights_wide.jpg\"]},{\"ID\":\"test2\",\"accuracy\":
	// 0.57,\"fpr\":[0, 0.3, 0.5, 1],\"tpr\":[0, 0.4, 0.7, 1],\"AUC\":0.57,
	// \"trainingSize\":
	// 150,\"threshold\":0.4,\"falsepositive\":[\"img/408757582443373.jpg\",\"img/6471154175007223.jpg\",\"img/img_nature_wide.jpg\",\"img/img_fjords_wide.jpg\"],\"falsenegative\":[\"img/7169059395095464.jpg\",\"img/8119543326477369.jpg\",\"img/8433457349861016.jpg\",\"img/img_nature_wide.jpg\",\"img/img_fjords_wide.jpg\"]},{\"ID\":\"test3\",\"accuracy\":
	// 0.67,\"fpr\":[0, 0.1, 0.8, 1],\"tpr\":[0, 0.4, 0.7, 1],\"AUC\":0.77,
	// \"trainingSize\":
	// 250,\"threshold\":0.5,\"falsepositive\":[\"img/img_nature_wide.jpg\",\"img/img_fjords_wide.jpg\"],\"falsenegative\":[\"img/img_mountains_wide.jpg\",\"img/img_lights_wide.jpg\"]},{\"ID\":\"test4\",\"accuracy\":
	// 0.89,\"fpr\":[0, 0.4, 0.6, 1],\"tpr\":[0, 0.4, 0.7, 1],\"AUC\":0.97,
	// \"trainingSize\": 350,
	// \"threshold\":0.54,\"falsepositive\":[\"img/img_nature_wide.jpg\",\"img/img_fjords_wide.jpg\"],\"falsenegative\":[\"img/img_mountains_wide.jpg\",\"img/img_lights_wide.jpg\"]},{\"ID\":\"test5\",\"accuracy\":
	// 0.99,\"fpr\":[0, 0.6, 0.8, 1],\"tpr\":[0, 0.4, 0.7, 1],\"AUC\":0.999,
	// \"trainingSize\": 550,
	// \"threshold\":0.99,\"falsepositive\":[\"img/img_nature_wide.jpg\",\"img/img_fjords_wide.jpg\"],\"falsenegative\":[\"img/img_mountains_wide.jpg\",\"img/img_lights_wide.jpg\",\"img/408757582443373.jpg\",\"img/6471154175007223.jpg\"]}]";
	//
	// // String path = getServletContext().getContextPath();
	// // String path = getServletContext().getRealPath("/");
	// // System.out.println(path);
	// // JsonElement jelement = new JsonParser().parse(new FileReader(new
	// // File(path + "/json/testresult.json")));
	// // JsonObject o = jelement.getAsJsonObject();
	//
	// // return o;
	//
	// return s;
	// }

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
	private double computeAndrea(List<Double> tprTrace, List<Double> fprTrace) {

		double auc = 0.0;

		for (int i = 0; i < (tprTrace.size() - 1); i++)
			auc += ((fprTrace.get(i + 1) - fprTrace.get(i)) * (tprTrace.get(i + 1) + tprTrace.get(i)) / 2);

		// System.out.println("[GetTestResult computeAUC()] auc = " + auc);

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

		System.out.println("[GetTestResult computeAUCTrapezio()] auc = " + auc);

		// TODO

		return auc;
	}

}
