package myservlet;

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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import datamodel.Dataset;
import services.CloudantClientMgr;

/**
 * Servlet implementation class GetDataset
 */
@WebServlet("/GetDataset")
public class GetDataset extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetDataset() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Valori possibile:
		// training_set
		// test_set
		String datasetType = request.getParameter("sub_type");

		// Ricevi get con parametro sub_type
		Database db = CloudantClientMgr.getDB();

		
		// Condizione
		String selector = "{\"selector\": {\"sub_type\":\"" + datasetType + "\"}}";

		// debug, query
//		System.out.println("Query:  -> " + selector);
  
		Gson gson = new Gson();

        // Limita i campi
        FindByIndexOptions o = new FindByIndexOptions()
        	 .fields("_id").fields("label").fields("images");
        
        // execute query
        //List<Dataset> datasets = db.findByIndex(selector, Dataset.class, o);

		//----------------------------------------------------------
		// Se non mi va bene il default e lo devo modificare:
        JsonArray arr = gson.toJsonTree(db.findByIndex(selector, Dataset.class, o)).getAsJsonArray();
//
        for(int i = 0; i < arr.size(); i++)
        {
        	JsonObject obj = arr.get(i).getAsJsonObject();
        	JsonObject images = obj.getAsJsonObject("images");
//        	System.out.println(images.toString());
                	
        	// TODO qui dobbiamo fare una query decente.. cosi è una cosa veloce
        	int size = images.getAsJsonArray("positive").size() + images.getAsJsonArray("negative").size();
        	
        	obj.remove("images");        	
        	obj.addProperty("trainingSize", size);

        }
		response.getWriter().append(arr.toString());
		//----------------------------------------------------------

//		response.getWriter().append(gson.toJson(datasets));
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
