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
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.mybluemix.visualrecognitiontester.blmxservices.CloudantClientMgr;
import net.mybluemix.visualrecognitiontester.datamodel.Classifier;
import net.mybluemix.visualrecognitiontester.datamodel.Dataset;


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
		
		// Condizione
		String selector;
		if(classifierId.isEmpty()) {
			System.out.println("[CheckOverlapImages] classifier ID doesn't exist");
			JsonObject o = new JsonObject();
			o.addProperty("error", "classifier ID doesn't exist");
			response.getWriter().println(o);
			return;
        }
		
		Classifier c = db.find(Classifier.class,classifierId);
		String classifierDatasetId=c.getTrainingSet();
		
		Dataset d = db.find(Dataset.class,classifierDatasetId);
		
		//prendi le immagini positive e negative
		//d.getPositiveSize()
		
		//prendi le immagini positive e negative dell'altro dataset
		//scorri per contare quanti ID comuni ci sono
		//pubblichi il risultato nel campo images di result
		
		Gson gson = new Gson();

        // Limita i campi
//        FindByIndexOptions opt = new FindByIndexOptions()
//        	 .fields("_id").fields("label")
//        	 .fields("training_size").fields("status")
//        	 .fields("comments").fields("shortname")
//        	 .fields("description").fields("training_set");
        
        // execute query
        //List<Classifier> classifiers = db.findByIndex(selector, Classifier.class, opt);

		//----------------------------------------------------------
		// Se non mi va bene il default e lo devo modificare:
    //    JsonArray arr = gson.toJsonTree(db.findByIndex(selector, Dataset.class, opt)).getAsJsonArray();
        
//		response.getWriter().append(gson.toJson(classifiers));

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
