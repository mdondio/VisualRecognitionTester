package net.mybluemix.visualrecognitiontester.servlet;

import java.io.IOException;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassification;

import net.mybluemix.visualrecognitiontester.blmxservices.CloudantClientMgr;
import net.mybluemix.visualrecognitiontester.blmxservices.ObjectStorage;
import net.mybluemix.visualrecognitiontester.blmxservices.ObjectStorageClientMgr;
import net.mybluemix.visualrecognitiontester.datamodel.Classifier;
import net.mybluemix.visualrecognitiontester.datamodel.Dataset;
import net.mybluemix.visualrecognitiontester.marcovisualreclibrary.Utils;
import net.mybluemix.visualrecognitiontester.marcovisualreclibrary.WatsonBinaryClassificationResult;
import net.mybluemix.visualrecognitiontester.marcovisualreclibrary.WatsonBinaryClassificationResult.METRIC;
import net.mybluemix.visualrecognitiontester.marcovisualreclibrary.WatsonBinaryClassifier;

/**
 * This servlet will launch a classification on Watson Visual Recognition Service
 */
@WebServlet("/Classify")
public class Classify extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Classify() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		
		
		// TODO DA QUI: ho preso una cantonata.. mi servono i ground truth nei dataset di test!!!!!
		// TODO DA QUI: ho preso una cantonata.. mi servono i ground truth nei dataset di test!!!!!
		// TODO DA QUI: ho preso una cantonata.. mi servono i ground truth nei dataset di test!!!!!
		// TODO DA QUI: ho preso una cantonata.. mi servono i ground truth nei dataset di test!!!!!
		// TODO DA QUI: ho preso una cantonata.. mi servono i ground truth nei dataset di test!!!!!
		// TODO DA QUI: ho preso una cantonata.. mi servono i ground truth nei dataset di test!!!!!
		// TODO DA QUI: ho preso una cantonata.. mi servono i ground truth nei dataset di test!!!!!
		// TODO DA QUI: ho preso una cantonata.. mi servono i ground truth nei dataset di test!!!!!
		
		
		
		System.out.println("[Classify doGet()] Function called");

		// test purpose XXX
		// qui id del dataset - api key / altro per usare uno specifico classificatore
		HashMap<String, String> pairs = new HashMap<String, String>();
		pairs.put("helicopter_test01", "helicopter_a40fce6329c185129d0d6ac72f4a4b22d23ffba1");
		
		// Create result object
		JsonObject o = new JsonObject();
		JsonArray results = new JsonArray();
		
		// For each pair..
		for(String testSetId : pairs.keySet()){
			
			String classifierID = pairs.get(testSetId);
			
			Dataset d = retrieveTestSet(testSetId);
			
			if(d == null)
				continue;
			
			// Generate the zip files (20 images per file max)
			List<byte[]> zipFiles = generateZipTestSet(d);
			
			// We can run a classification
			// TODO: apiKey fortemente legato a classifierID
			// mie
			 String apiKey = "64d19574fb8f93071a94a461e479ff6c6885d78b";
			 String label = "helicopter";
			 
			// Luca credenziali
//			public static final String api_key = "a40fce6329c185129d0d6ac72f4a4b22d23ffba1";
			// Andrea credenziali
//			public static final String api_key = "db57c99c1cb2fd47e15e4625c4c0b726cc38f40e";

			JsonObject classificationResult = runClassification(apiKey, classifierID, label, zipFiles);
			
			// Add result to array
			results.add(classificationResult);
	}
		// Add all results
		o.add("results", results);
		
		// return json
		response.getWriter().append(o.toString());

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	// XXX ottimizza e recupera tutti i testSet indicati con una sola query
	private Dataset retrieveTestSet(String id){

		// Ricevi get con parametro sub_type
		Database db = CloudantClientMgr.getCloudantDB();

		// Condizione
		String selector = "{\"selector\": {\"_id\" : \""+id+"\", \"type\":\"dataset\", \"subtype\":\"test_set\"}}";

		Gson gson = new Gson();
        // Limita i campi
        FindByIndexOptions opt = new FindByIndexOptions()
        	 .fields("_id").fields("images");
        
        // execute query
        List<Dataset> classifiers = db.findByIndex(selector, Dataset.class, opt);
        
        return classifiers == null? null : classifiers.get(0);
	}

	// Generate zip files from a dataset
	private List<byte[]> generateZipTestSet(Dataset d) throws IOException{

		// Get instance
		ObjectStorage oo = ObjectStorageClientMgr.getObjectStorage();

		// Get all images
		List<Long> images = d.getImages().getPositives();
		images.addAll(d.getImages().getNegatives());
		
		// return list of zips: each zip contains 20 images (max per call)
		return Utils.buildCompressedStreamBlocks(oo, "images_london", images);
	}
	
	
	
	// This method runs a classification on watson
	private JsonObject runClassification(String apiKey, String classifierID, String label, HashMap<Long, Boolean> testSet, List<byte[]> zipFiles) throws IOException {

		// Istantiate service on BlueMix
		WatsonBinaryClassifier classifier = new WatsonBinaryClassifier(apiKey);
		classifier.setClassifierId(classifierID);
		classifier.setLabel(label);
		
		// Now 

		// Classify all zips
		List<VisualClassification> watsonres = classifier.classify(zipFiles, Utils.WATSONMINSCORE);


		// Compute results and metrics
		double minThreshold = 0.05;
		double maxThreshold = 0.6;
		double step = 0.05;

		DecimalFormat df = new DecimalFormat("#.###");

		List<Double> tprs = new ArrayList<Double>();
		List<Double> fprs = new ArrayList<Double>();

		for (double threshold = minThreshold; threshold <= maxThreshold; threshold += step) {

			WatsonBinaryClassificationResult result = new WatsonBinaryClassificationResult(label, testSet, watsonres,
					threshold);

			double curTpr = result.computeMetric(METRIC.tpr);
			double curFpr = result.computeMetric(METRIC.fpr);
			tprs.add(curTpr);
			fprs.add(curFpr);

			System.out.println("-------------------------------");
			System.out.println("threshold: " + df.format(threshold));
			System.out.println("tpr: " + df.format(curTpr));
			System.out.println("fpr: " + df.format(curFpr));
			System.out.println("-------------------------------");

		}
		
		
		return buildJSON(tprs, fprs, label,trainingSetSize, testSetSize);
	}

	
	// TODO da sistemare...
	private JsonObject buildJSON(List<Double> tprs, List<Double> fprs, String label, int trainingSetSize, int testSetSize) throws IOException {

		JsonObject obj = new JsonObject();

		JsonObject test = new JsonObject();

		JsonObject plot = new JsonObject();
		
		plot.addProperty("mode", "lines");
		plot.addProperty("name", label + "_training-" + 2 * trainingSetSize + "_test-" + 2 * testSetSize);

		JsonObject line = new JsonObject();
		line.addProperty("shape", "spline");
		line.addProperty("color", "rgb(55, 128, 191)");
		plot.add("line", line);

		// Build x series (fprs)
		// Build x series (tprs)

		JsonArray fprsArray = new JsonArray();
		JsonArray tprsArray = new JsonArray();

		for (Double d : fprs)
			fprsArray.add(d);

		for (Double d : tprs)
			tprsArray.add(d);

		plot.add("x", fprsArray);
		plot.add("y", tprsArray);

		test.add("plot", plot);

		// TODO
		test.add("threshold", buildThreshold());
		test.add("AUC", buildAUC());

		obj.add(label + "_training-" + 2 * trainingSetSize + "_test-" + 2 * testSetSize, test);

		return obj;

	}
	
	private JsonObject buildThreshold() {
		JsonObject threshold = new JsonObject();

		JsonArray x = new JsonArray();
		x.add(0.0);
		threshold.add("x", x);
		
		JsonArray y = new JsonArray();
		y.add(0.0);
		threshold.add("y", y);

		threshold.addProperty("mode", "markers");
		threshold.addProperty("name", "Threshold TODO");

		JsonObject marker = new JsonObject();
		marker.addProperty("color", "rgb(55, 128, 191)");
		marker.addProperty("size", "12");

		threshold.add("marker", marker);

		return threshold;

	}
	private  JsonObject buildAUC() {
		JsonObject auc = new JsonObject();

		JsonArray x = new JsonArray();
		x.add(0.0);
		auc.add("x", x);
		
		JsonArray y = new JsonArray();
		y.add(0.0);
		auc.add("y", y);


		auc.addProperty("mode", "markers");
		auc.addProperty("name", "AUC TODO");

		JsonObject marker = new JsonObject();
		marker.addProperty("color", "rgb(55, 128, 191)");
		marker.addProperty("size", "12");

		auc.add("marker", marker);

		return auc;
	}
}
