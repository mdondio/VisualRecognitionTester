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

import net.mybluemix.visualrecognitiontester.blmxservices.CloudantClientMgr;
import net.mybluemix.visualrecognitiontester.datamodel.Dataset;


/**
 * This endpoint retrieves Datasets. 
 * @author Marco Dondio
 */
@WebServlet("/GetDataset")
public class GetDataset extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetDataset() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		System.out.println("[GetDataset doGet()] Function called");

	//	String datasetType = request.getParameter("sub_type");

		// Ricevi get con parametro sub_type
		Database db = CloudantClientMgr.getCloudantDB();
		String datasetId = request.getParameter("datasetId");
		
		// Condizione con ricerca di Id
		String selector = "{\"selector\": {\"type\":\"dataset\"" + (datasetId == null ? "" : ", \"_id\":\"" + datasetId + "\"")+"}}";

		// temporaneo, poi verrà rimosso
		//String selector = "{\"selector\": {\"type\":\"dataset\"}}";

		// debug, query
//		System.out.println("Query:  -> " + selector);
  

        // Limita i campi
        FindByIndexOptions o = new FindByIndexOptions()
        	 .fields("_id").fields("label").fields("images");
        
        // execute query
        List<Dataset> datasets = db.findByIndex(selector, Dataset.class, o);
        
        
        
        response.getWriter().append(CloudantClientMgr.getGsonBuilder().create().toJson(datasets));
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
