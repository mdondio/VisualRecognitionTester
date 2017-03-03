package net.mybluemix.visualrecognitiontester.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.FindByIndexOptions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.mybluemix.visualrecognitiontester.blmxservices.CloudantClientMgr;
import net.mybluemix.visualrecognitiontester.datamodel.Classifier;
import net.mybluemix.visualrecognitiontester.datamodel.Dataset;
import net.mybluemix.visualrecognitiontester.datamodel.Instance;


/**
 * Servlet implementation class RetrieveClassifiers
 */
@WebServlet("/GetInstance")
public class GetInstance extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetInstance() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		System.out.println("[GetInstance doGet()] Function called");
		

		// Ricevi get con parametro sub_type
		Database db = CloudantClientMgr.getDB();

		// Condizione
		String selector = "{\"selector\": {\"type\":\"instance\"}}";

		
		// debug, query
//		System.out.println("Query:  -> " + selector);
  
		Gson gson = new Gson();

        // Limita i campi
        FindByIndexOptions opt = new FindByIndexOptions()
        	 .fields("_id").fields("classifiers");
        
        // execute query
        List<Instance> instances = db.findByIndex(selector, Instance.class, opt);

		//----------------------------------------------------------
		// Se non mi va bene il default e lo devo modificare:
    //    JsonArray arr = gson.toJsonTree(db.findByIndex(selector, Dataset.class, opt)).getAsJsonArray();
        
		response.getWriter().append(gson.toJson(instances));
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	
	 private  JSONObject readJSONfromString(String json) {
		 
		 
	        JSONParser parser = new JSONParser();

      JSONObject jsonObject = null;

	            Object obj;
				try {
					obj = parser.parse(json);
		             jsonObject =  (JSONObject) obj;

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


	             /*
	            String name = (String) jsonObject.get("name");
	            System.out.println(name);

	            String city = (String) jsonObject.get("city");
	            System.out.println(city);

	            String job = (String) jsonObject.get("job");
	            System.out.println(job);

	            // loop array
	            JSONArray cars = (JSONArray) jsonObject.get("cars");
	            Iterator<String> iterator = cars.iterator();
	            while (iterator.hasNext()) 
	             System.out.println(iterator.next());
	             */
	      
	        
	    
	 
	 return jsonObject;
	 }
	 
	 

}
