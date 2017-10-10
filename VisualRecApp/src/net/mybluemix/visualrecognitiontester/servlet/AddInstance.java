package net.mybluemix.visualrecognitiontester.servlet;

import java.io.IOException;
import java.util.LinkedList;
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
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassifier;

import net.mybluemix.visualrecognitiontester.blmxservices.CloudantClientMgr;
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
			throws ServletException, IOException{

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
		

		VisualRecognition service = new VisualRecognition(VisualRecognition.VERSION_DATE_2016_05_20);
		service.setApiKey(apiKey);
//		System.out.println("[AddInstance doGet()] risultato getEndPoint: "+service.getEndPoint());

		List<VisualClassifier> result = null;
		//result=service.getClassifiers().execute();
		try{
		result=service.getClassifiers().execute();
//		result = service.classify(options).execute();
		}		
		catch(Exception e) {
				System.out.println("[AddInstance] "+e.getMessage());
				JsonObject o = new JsonObject();
				o.addProperty("error", e.getMessage());
				response.getWriter().println(o);
				return;
		}
		

		String selector;
		selector = "{\"selector\": {\"type\":\"visual recognition instance\", \"api_key\":\"" + apiKey + "\"}}";
		Database db = CloudantClientMgr.getCloudantDB();
        FindByIndexOptions opt = new FindByIndexOptions().fields("_id");
        List<Instance> instances = db.findByIndex(selector, Instance.class, opt);
        System.out.println(instances);
        if(instances.size()>0){
        	System.out.println("[AddInstance] api_key already available");
			JsonObject o = new JsonObject();
			o.addProperty("error", "api_key already available");
			response.getWriter().println(o);
			return;
        }
        
		Instance newInstance = new Instance();
		newInstance.setType("visual recognition instance");
		newInstance.setAccount("account_account");
		newInstance.setRegion("region");
		newInstance.setId("vr_instance_"+apiKey);
		newInstance.setClassifier(new LinkedList<String>());
		newInstance.setApikey(apiKey);
		Response responsePost = db.post(newInstance);
		System.out.println("[AddInstance] New instance added to DB: "+responsePost);
		JsonObject o = new JsonObject();
		o.addProperty("message", "New instance added, done!");
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

}
