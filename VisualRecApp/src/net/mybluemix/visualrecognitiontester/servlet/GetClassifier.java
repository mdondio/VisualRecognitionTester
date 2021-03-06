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

import net.mybluemix.visualrecognitiontester.blmxservices.CloudantClientMgr;
import net.mybluemix.visualrecognitiontester.datamodel.Classifier;


/**
 * This endpoint retrieves Classifiers. You can use the parameter _id to select just one classifier. 
 * @author Marco Dondio & Andrea Bortolossi
 * Test with:
 * http://localhost:9080/VisualRecognitionTester/GetClassifier?_id=Wind_classifier_1184422983
 */
@WebServlet("/GetClassifier")
public class GetClassifier extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetClassifier() {
        super();

    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		System.out.println("[GetClassifier doGet()] Function called");
		
		Database db = CloudantClientMgr.getCloudantDB();
		
		// First, parse args
		String classifierId = request.getParameter("_id");
		
		// Condizione
		String selector;
		if(classifierId.isEmpty()) selector = "{\"selector\": {\"type\":\"classifier\"}}";
		else selector = "{\"selector\": {\"type\":\"classifier\", \"_id\":\"" + classifierId + "\"}}";
		//TODO aggiungere warning se ID classificatore non esiste
		
		Gson gson = new Gson();

        // Limita i campi
        FindByIndexOptions opt = new FindByIndexOptions()
        	 .fields("_id").fields("label")
        	 .fields("training_size").fields("status")
        	 .fields("comments").fields("shortname")
        	 .fields("description").fields("training_set");
        
        // execute query
        List<Classifier> classifiers = db.findByIndex(selector, Classifier.class, opt);

		//----------------------------------------------------------
		// Se non mi va bene il default e lo devo modificare:
    //    JsonArray arr = gson.toJsonTree(db.findByIndex(selector, Dataset.class, opt)).getAsJsonArray();
        
		response.getWriter().append(gson.toJson(classifiers));

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
