package net.mybluemix.visualrecognitiontester.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.FindByIndexOptions;
import com.cloudant.client.api.model.Response;
import com.google.gson.JsonObject;

import net.mybluemix.visualrecognitiontester.backgroundaemons.datamodel.Job;
import net.mybluemix.visualrecognitiontester.backgroundaemons.datamodel.JobQueue;
import net.mybluemix.visualrecognitiontester.backgroundaemons.datamodel.TrainingJobInfo;
import net.mybluemix.visualrecognitiontester.blmxservices.CloudantClientMgr;
import net.mybluemix.visualrecognitiontester.datamodel.Classifier;
import net.mybluemix.visualrecognitiontester.datamodel.Dataset;
import net.mybluemix.visualrecognitiontester.datamodel.Instance;

/**
 * This endpoint is used to edit a classifier (change of description/shortname/comments/label). If the field doesn't exist yet you can't edit it (or create it)
 * @author Andrea Bortolossi
 * Test with:
 * http://localhost:9080/VisualRecognitionTester/UpdateClassifier?_id=Wind_classifier_639040266&shortname=myclassifier&label=&description=BLABALBAL and this more than that&comments=
 */
@WebServlet("/UpdateClassifier")
public class UpdateClassifier extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UpdateClassifier() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		// First, parse args
		String _Id = request.getParameter("_id");
		
		String shortname = request.getParameter("shortname");
		String label = request.getParameter("label");
		String description = request.getParameter("description");
		String comments = request.getParameter("comments");

		if(_Id==null)
		{
			System.out.println("[UpdateClassifier] Classifier Id is NULL");
			return;
		}
	
		// get db connection
		Database db = CloudantClientMgr.getCloudantDB();
		
		Classifier c = db.find(Classifier.class,_Id);
		
		if(!shortname.isEmpty()) c.setDescription(shortname);
		if(!label.isEmpty()) c.setDescription(label);
		if(!description.isEmpty()) c.setDescription(description);
		if(!comments.isEmpty()) c.setDescription(comments);
		
		Response responseUpdate = db.update(c);
		
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
