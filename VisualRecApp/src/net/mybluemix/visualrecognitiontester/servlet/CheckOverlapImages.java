package net.mybluemix.visualrecognitiontester.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.FindByIndexOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.mybluemix.visualrecognitiontester.blmxservices.CloudantClientMgr;
import net.mybluemix.visualrecognitiontester.datamodel.Classifier;
import net.mybluemix.visualrecognitiontester.datamodel.Dataset;
import net.mybluemix.visualrecognitiontester.datamodel.Images;


/**
 * This endpoint check if dataset images are already included in training dataset used by classifier 
 * @author Andrea Bortolossi
 * Test with:
 * http://localhost:9080/VisualRecognitionTester/CheckOverlapImages?dataset=IDdataset&classifier=Wind_classifier_1184422983&
 */
@WebServlet("/CheckOverlapImages")
public class CheckOverlapImages extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CheckOverlapImages() {
        super();

    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		System.out.println("[CheckOverlapImages doGet()] Function called");
		
		Database db = CloudantClientMgr.getCloudantDB();
		
		// First, parse args
		String datasetId = request.getParameter("dataset");
		String classifierId = request.getParameter("classifier");
		
		if(classifierId.isEmpty()) {
			System.out.println("[CheckOverlapImages] classifier ID doesn't exist");
			JsonObject o = new JsonObject();
			o.addProperty("error", "classifier ID doesn't exist");
			response.getWriter().println(o);
			return;
        }
		if(datasetId.isEmpty()) {
			System.out.println("[CheckOverlapImages] dataset ID doesn't exist");
			JsonObject o = new JsonObject();
			o.addProperty("error", "dataset ID doesn't exist");
			response.getWriter().println(o);
			return;
        }
		
		Classifier c = db.find(Classifier.class,classifierId);
		String classifierDatasetId=c.getTrainingSet();
		
		Dataset dClassifier = db.find(Dataset.class,classifierDatasetId);
		List<Long> imgClassifier = new ArrayList<Long>();
		imgClassifier.addAll(dClassifier.getImages().getPositives());
		imgClassifier.addAll(dClassifier.getImages().getNegatives());
		
		Dataset dDataset = db.find(Dataset.class,datasetId);
		List<Long> imgDataset = new ArrayList<Long>();
		imgDataset.addAll(dDataset.getImages().getPositives());
		imgDataset.addAll(dDataset.getImages().getNegatives());
		
		imgDataset.retainAll(imgClassifier);
		System.out.println("[CheckOverlapImages] The following "+imgDataset.size()+" are the common ID images"+imgDataset);
		
		Gson gson = new Gson();
		JsonObject o = new JsonObject();
		o.addProperty("images", gson.toJson(imgDataset));
		o.addProperty("size", imgDataset.size());
		response.getWriter().println(o);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
