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
import net.mybluemix.visualrecognitiontester.datamodel.Classifier;
import net.mybluemix.visualrecognitiontester.datamodel.Instance;

/**
 * This endpoint checks DB integrity
 * 
 * @author Marco Dondio
 */
@WebServlet("/CheckDb")
public class CheckDb extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CheckDb() {
		super();

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		System.out.println("[CheckDb doGet()] Function called");

		
		
		allClassifiersContained();
		allInstancesMapped();
		

	}

	private void allClassifiersContained() {
		Database db = CloudantClientMgr.getCloudantDB();

		// Condizione
		String selector = "{\"selector\": {\"type\":\"classifier\"}}";

		// debug, query
		// System.out.println("Query: -> " + selector);


		// Limita i campi
		FindByIndexOptions opt = new FindByIndexOptions().fields("_id").fields("instance").fields("status");

		// execute query
		List<Classifier> classifiers = db.findByIndex(selector, Classifier.class, opt);

		for (Classifier c : classifiers) {

			System.out.println("[CheckDb doGet()] Checking " + c.getID() + "("+c.getApiKey()+")");

			selector = "{\"selector\": {\"_id\":\"vr_instance_" + c.getApiKey() + "\"}}";
			
			opt = new FindByIndexOptions().fields("_id").fields("classifiers");

			// execute query
			List<Instance> instances = db.findByIndex(selector, Instance.class, opt);

			Instance i = instances.get(0);
			
			if (!i.getClassifiers().contains(c.getID()))
				System.out.println("[CheckDb doGet()] vr_instance_" + c.getApiKey() + " does not contain classifierId "
						+ c.getID());

		}
		
	}
	
	private void allInstancesMapped() {
		
		Database db = CloudantClientMgr.getCloudantDB();

		// Condizione
		String selector = "{\"selector\": {\"type\":\"visual recognition instance\"}}";

		// Limita i campi
		FindByIndexOptions opt = new FindByIndexOptions().fields("_id").fields("api_key").fields("classifiers");

		// execute query
		List<Instance> instances = db.findByIndex(selector, Instance.class, opt);

		for (Instance i : instances) {

			System.out.println("[CheckDb doGet()] Checking instance " + i.getId());

			for(String classifierId : i.getClassifiers()){

				System.out.println("[CheckDb doGet()] Checking for classifier " + classifierId);

				// Condizione
				 selector = "{\"selector\": {\"_id\":\""+classifierId+"\"}}";
					 opt = new FindByIndexOptions().fields("_id").fields("instance");

						List<Classifier> classifiers = db.findByIndex(selector, Classifier.class, opt);
						
						if(classifiers.isEmpty())
						{
							System.out.println("[CheckDb doGet()] Classifier not found:" + classifierId);
							continue;
						}
						
						if(!classifiers.get(0).getApiKey().equals(i.getApiKey()))
								{
							System.out.println("[CheckDb doGet()] API KEY NOT MATCH!");
							continue;
						}
			}

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
