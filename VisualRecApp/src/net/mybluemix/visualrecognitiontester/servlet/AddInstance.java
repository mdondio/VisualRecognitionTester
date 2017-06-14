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
import com.cloudant.client.api.model.Response;
import com.google.gson.JsonObject;
import com.ibm.watson.developer_cloud.service.exception.BadRequestException;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyImagesOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassification;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassifier;

import net.mybluemix.visualrecognitiontester.blmxservices.CloudantClientMgr;
import net.mybluemix.visualrecognitiontester.blmxservices.marcovisualreclibrary.WatsonBinaryClassifier;
import net.mybluemix.visualrecognitiontester.blmxservices.marcovisualreclibrary.exceptions.VisualClassifierException;
import net.mybluemix.visualrecognitiontester.datamodel.Classifier;
import net.mybluemix.visualrecognitiontester.datamodel.Dataset;
import net.mybluemix.visualrecognitiontester.datamodel.Images;
import net.mybluemix.visualrecognitiontester.datamodel.Instance;

/**
 * This endpoint add an instance of VisualRecognition to the cloudant DB
 * http://localhost:9080/VisualRecognitionTester/AddInstance?api_key=ID1112344455
 * @author Andrea Bortolossi
 */
@WebServlet("/AddInstance")
public class AddInstance extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AddInstance() {
		super();

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		System.out.println("[AddInstance doGet()] Function called");
		
		// First, get classifierId
		String apiKey = request.getParameter("api_key");

		if (apiKey == null) {
			System.out.println("[AddInstance] No api_key specified");
			JsonObject o = new JsonObject();
			o.addProperty("error", "no api_key specified");
			response.getWriter().println(o);
			return;
		}

//		Database db = CloudantClientMgr.getCloudantDB();
		
		Instance newInstance = new Instance();
		newInstance.setType("visual recognition instance");
//		newInstance.setAccount(account);
//		newInstance.setRegion(region);
		newInstance.setApikey(apiKey);
		VisualRecognition service = new VisualRecognition(VisualRecognition.VERSION_DATE_2016_05_20);
		service.setApiKey(apiKey);
//		System.out.println("[AddInstance doGet()] risultato getEndPoint: "+service.getEndPoint());

		List<VisualClassifier> result = null;
		result=service.getClassifiers().execute();
		try{
		result=service.getClassifiers().execute();
//		result = service.classify(options).execute();
		}		
		catch(BadRequestException e) {
				throw new VisualClassifierException("[AddInstance] VisualClassification: Bad request - " + e.getMessage());
		}
		
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
