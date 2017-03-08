package net.mybluemix.visualrecognitiontester.servlet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
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
import net.mybluemix.visualrecognitiontester.datamodel.DatasetLong;

/**
 * This servlet will launch a classification on Watson Visual Recognition Service
 */
@WebServlet("/GetTestResult")
public class GetTestResult extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetTestResult() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		System.out.println("[GetTestResult doGet()] Function called");

		// parse request: a list of testSet - classifier
		HashMap<String, String> pairs = parseRequest(request);
		
		// Create result object
	//	JsonObject o = new JsonObject();
		JsonArray results = new JsonArray();
		
		// For each pair..
		for(String testSetId : pairs.keySet()){
			
			// retrieve dataset and classifier object
			DatasetLong testSet = retrieveTestSet(testSetId);
			
			//-----------------------
			// debug
//			System.out.println("------------------------");
//			System.out.println(testSet.getId());
//
//			for(Long img : testSet.getImages().getPositives())
//			System.out.println("positive: " + Long.toUnsignedString(img));
//			for(Long img : testSet.getImages().getNegatives())
//			System.out.println("negative: " + Long.toUnsignedString(img));
//			System.out.println("------------------------");

			//-----------------------

			Classifier classifier = retrieveClassifier(pairs.get(testSetId));
			
			if(testSet == null || classifier == null)
				continue;
			
			// Generate the zip files (20 images per file max)
			List<byte[]> zipFiles = generateZipTestSet(testSet);
			
			// We can run a classification
			JsonObject classificationResult = runClassification(classifier, testSet, zipFiles);
			
			// Add result to array
			results.add(classificationResult);
	}
		

		// CORRETTO DA USARE
		//response.getWriter().println(results);

		
		// XXX togliere
		response.getWriter().append(testResult());
	}


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	
	// This function translates request into an useful data structure
	private HashMap<String, String> parseRequest(HttpServletRequest request){
		
		System.out.println(request.getQueryString());

		
		////////////////////////////////////////////////////////
		// test purpose XXX
		// qui id del dataset - classifier ID / altro per usare uno specifico classificatore
		HashMap<String, String> pairs = new HashMap<String, String>();
		pairs.put("watch_test01", "watch_classifier_1559642317");
		
//		http://localhost:9080/VisualRecognitionTester/show.html?
		//arr=a,b,yyxxxxxxx,yyxxxxxxx,yyxxxxxxx,xxxxxxxxx
		
		// diviso 3 = numero di triplette
		
		////////////////////////////////////////////////////////
return pairs;
		
	} 
	
	// XXX ottimizza e recupera tutti i testSet indicati con una sola query
	private DatasetLong retrieveTestSet(String id){

		// Ricevi get con parametro sub_type
		Database db = CloudantClientMgr.getCloudantDB();

		// Condizione
		String selector = "{\"selector\": {\"_id\" : \""+id+"\"}}";

		
        // Limita i campi
        FindByIndexOptions opt = new FindByIndexOptions()
        	 .fields("_id").fields("images");
        
        // execute query
        List<Dataset> datasets = db.findByIndex(selector, Dataset.class, opt);
        
        
        // TODO convert Dataset to DatasetLong
        List<DatasetLong> datasetsLong = DatasetLong.convertFromDatasets(datasets);
         
        
        return datasetsLong == null? null : datasetsLong.get(0);
	}

	// Recupera il classificatore
	private Classifier retrieveClassifier(String id) {
		// Ricevi get con parametro sub_type
		Database db = CloudantClientMgr.getCloudantDB();

		// Condizione
		String selector = "{\"selector\": {\"_id\" : \""+id+"\"}}";

        // Limita i campi
        FindByIndexOptions opt = new FindByIndexOptions()
        	 .fields("_id").fields("instance").fields("label").fields("training_size").fields("training_set");
        
        // execute query
        List<Classifier> classifiers = db.findByIndex(selector, Classifier.class, opt);
        
        return classifiers == null? null : classifiers.get(0);
	}

	
	
	
	// Generate zip files from a dataset
	private List<byte[]> generateZipTestSet(DatasetLong d) throws IOException{

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
	private JsonObject runClassification(Classifier classifierJson, DatasetLong testSet, List<byte[]> zipFiles) throws IOException {

		// Istantiate service on BlueMix
		WatsonBinaryClassifier classifier = new WatsonBinaryClassifier(classifierJson.getApiKey());
		classifier.setClassifierId(classifierJson.getID());
		classifier.setLabel(classifierJson.getLabel());
		
		// Classify all zips against Watson instance
		List<VisualClassification> watsonres = classifier.classify(zipFiles, Utils.WATSONMINSCORE);

		// Compute results and metrics
		double minThreshold = 0.05;
		double maxThreshold = 0.6;
		double step = 0.05;

//		DecimalFormat df = new DecimalFormat("#.###");

		List<Double> tprs = new ArrayList<Double>();
		List<Double> fprs = new ArrayList<Double>();

		for (double threshold = minThreshold; threshold <= maxThreshold; threshold += step) {

			
			WatsonBinaryClassificationResult result = new WatsonBinaryClassificationResult(testSet, watsonres,
					threshold);

			double curTpr = result.computeMetric(METRIC.tpr);
			double curFpr = result.computeMetric(METRIC.fpr);
			tprs.add(curTpr);
			fprs.add(curFpr);

//			System.out.println("-------------------------------");
//			System.out.println("threshold: " + df.format(threshold));
//			System.out.println("tpr: " + df.format(curTpr));
//			System.out.println("fpr: " + df.format(curFpr));
//			System.out.println("-------------------------------");

		}
		
		return buildJsonResult(tprs, fprs, classifierJson, testSet);
//		return buildJSON(tprs, fprs, classifierJson, testSet);
		
	//	return testResult();
	}

	
	private JsonObject buildJsonResult(List<Double> tprs, List<Double> fprs, Classifier classifier, DatasetLong testSet){
		
		JsonObject result = new JsonObject();
		
		// TODO capire bene
//			{"ID":"test2","accuracy": 0.57,"fpr":[0, 0.3, 0.5, 1],"tpr":[0, 0.4, 0.7, 1],"AUC":0.57, "trainingSize": 150,"threshold":0.4,"falsepositive":["img/408757582443373.jpg","img/6471154175007223.jpg","img/img_nature_wide.jpg","img/img_fjords_wide.jpg"],"falsenegative":["img/7169059395095464.jpg","img/8119543326477369.jpg","img/8433457349861016.jpg","img/img_nature_wide.jpg","img/img_fjords_wide.jpg"]}

		
		return result;
	}
	
	
	// XXX refactor needed.
	private JsonObject buildJSON(List<Double> tprs, List<Double> fprs, Classifier classifier, DatasetLong testSet) throws IOException {

		String label = classifier.getLabel();
		int trainingSetSize = classifier.getTrainingSize();
		int testSetSize = testSet.getSize();
		
		JsonObject obj = new JsonObject();

		JsonObject test = new JsonObject();

		JsonObject plot = new JsonObject();
		
		plot.addProperty("mode", "lines");
		plot.addProperty("name", label + "_training-" +  trainingSetSize + "_test-" +  testSetSize);

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

		obj.add(label + "_training-" + trainingSetSize + "_test-" +  testSetSize, test);

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

	// to quickly test integration
	// XXX to be removed
	private String testResult() throws JsonIOException, JsonSyntaxException, FileNotFoundException{
		
		
		String s = "[{\"ID\":\"test1\",\"accuracy\": 0.87,\"fpr\":[0, 0.2, 0.4, 1],\"tpr\":[0, 0.4, 0.7, 1],\"AUC\":0.67,\"trainingSize\": 50, \"threshold\":0.2,\"falsepositive\":[\"img/img_nature_wide.jpg\",\"img/img_fjords_wide.jpg\"],\"falsenegative\":[\"img/img_mountains_wide.jpg\",\"img/img_lights_wide.jpg\"]},{\"ID\":\"test2\",\"accuracy\": 0.57,\"fpr\":[0, 0.3, 0.5, 1],\"tpr\":[0, 0.4, 0.7, 1],\"AUC\":0.57, \"trainingSize\": 150,\"threshold\":0.4,\"falsepositive\":[\"img/408757582443373.jpg\",\"img/6471154175007223.jpg\",\"img/img_nature_wide.jpg\",\"img/img_fjords_wide.jpg\"],\"falsenegative\":[\"img/7169059395095464.jpg\",\"img/8119543326477369.jpg\",\"img/8433457349861016.jpg\",\"img/img_nature_wide.jpg\",\"img/img_fjords_wide.jpg\"]},{\"ID\":\"test3\",\"accuracy\": 0.67,\"fpr\":[0, 0.1, 0.8, 1],\"tpr\":[0, 0.4, 0.7, 1],\"AUC\":0.77, \"trainingSize\": 250,\"threshold\":0.5,\"falsepositive\":[\"img/img_nature_wide.jpg\",\"img/img_fjords_wide.jpg\"],\"falsenegative\":[\"img/img_mountains_wide.jpg\",\"img/img_lights_wide.jpg\"]},{\"ID\":\"test4\",\"accuracy\": 0.89,\"fpr\":[0, 0.4, 0.6, 1],\"tpr\":[0, 0.4, 0.7, 1],\"AUC\":0.97, \"trainingSize\": 350, \"threshold\":0.54,\"falsepositive\":[\"img/img_nature_wide.jpg\",\"img/img_fjords_wide.jpg\"],\"falsenegative\":[\"img/img_mountains_wide.jpg\",\"img/img_lights_wide.jpg\"]},{\"ID\":\"test5\",\"accuracy\": 0.99,\"fpr\":[0, 0.6, 0.8, 1],\"tpr\":[0, 0.4, 0.7, 1],\"AUC\":0.999, \"trainingSize\": 550, \"threshold\":0.99,\"falsepositive\":[\"img/img_nature_wide.jpg\",\"img/img_fjords_wide.jpg\"],\"falsenegative\":[\"img/img_mountains_wide.jpg\",\"img/img_lights_wide.jpg\",\"img/408757582443373.jpg\",\"img/6471154175007223.jpg\"]}]";
		
		 	
//		String path =  getServletContext().getContextPath();
//		String path =  getServletContext().getRealPath("/");
//		System.out.println(path);
//		JsonElement jelement = new JsonParser().parse(new FileReader(new File(path + "/json/testresult.json")));
//		JsonObject o = jelement.getAsJsonObject();
		
//		return o;	
		
		return s;
	}
	
}
