package myservlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


/**
 * Servlet implementation class RetrieveClassifiers
 */
@WebServlet("/RetrieveClassifiers")
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

		System.out.println("[RetrieveClassifiers doGet()] Function called");
		
		 String myJSON = "{ \"maps\": { \"classifiers\": [ {\"ID\": \"xxxxxxxxx\", \"label\": \"Watch\", \"trainingSize\": 100, \"status\": \"ready\"}, {\"ID\": \"yyyyyyyyy\", \"label\": \"Watch\", \"trainingSize\": 1000, \"status\": \"training\"}, {\"ID\": \"xxxxxxxxx\", \"label\": \"Helicopter\", \"trainingSize\": 300, \"status\": \"ready\"}, {\"ID\": \"xxxxxxxxx\", \"label\": \"Mushroom\", \"trainingSize\": 300, \"status\": \"ready\"}, {\"ID\": \"xxxxxxxxx\", \"label\": \"Watch\", \"trainingSize\": 100, \"status\": \"training\"}, {\"ID\": \"xxxxxxxxx\", \"label\": \"Plane\", \"trainingSize\": 500, \"status\": \"ready\"}, {\"ID\": \"xxxxxxxxx\", \"label\": \"Tree\", \"trainingSize\": 1500, \"status\": \"ready\"}, {\"ID\": \"xxxxxxxxx\", \"label\": \"Flower\", \"trainingSize\": 2000, \"status\": \"training\"}, {\"ID\": \"xxxxxxxxx\", \"label\": \"Flower\", \"trainingSize\": 2000, \"status\": \"training\"} ], \"total\": [14] }, \"training_dataset\": { \"labels\": [\"Watch\",\"Helicopter\",\"Mushroom\", \"Horse\", \"Plane\", \"Tree\", \"Flower\"], \"max_cardinality\": [1000, 400, 1200, 500, 2500, 3000, 1000] } }";

				JSONObject obj  = readJSONfromString(myJSON);

		System.out.println(obj);
		System.out.println("[RetrieveClassifiers doGet()] Json loaded..");

		// TODO
		// genera JSON
		//una parte statica
		// una parte recuperata dal server
		
		// Per il test leggo il json dal file table_02.json
		
		//classifier
//		{
//		  "selector": {
//		    "type": "classifier",
//		    "label": "helicopter"
//		  },
//		  "fields": [
//		    "_id",
//		    "label",
//		    "trainingsize",
//		    "status"
//		  ]
//		}

		
		
		
		response.getWriter().append(obj.toJSONString());
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
