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
import com.google.gson.JsonObject;

import net.mybluemix.visualrecognitiontester.backgroundaemons.datamodel.Job;
import net.mybluemix.visualrecognitiontester.backgroundaemons.datamodel.JobQueue;
import net.mybluemix.visualrecognitiontester.backgroundaemons.datamodel.TrainingJobInfo;
import net.mybluemix.visualrecognitiontester.blmxservices.CloudantClientMgr;
import net.mybluemix.visualrecognitiontester.datamodel.Dataset;
import net.mybluemix.visualrecognitiontester.datamodel.Instance;

/**
 * This endpoint is used to submit a train job, analyze it and provide a
 * feedback to user. Actual job will be done asynchronously in background
 * 
 * @author Marco Dondio
 * Test with:
 * http://localhost:9080/VisualRecognitionTester/SubmitTrainJob?datasetId=Wind_test_20&label=wind&description=pale eoliche in azione&shortname=pala1
 */
@WebServlet("/SubmitTrainJob")
public class SubmitTrainJob extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SubmitTrainJob() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		
//		temp("vr_instance_0b915860705c066d9bd0166ac4850eefc8717438", "watch_classifier_1508232015");
		
		// First, parse args
		String datasetId = request.getParameter("datasetId");
		String label = request.getParameter("label");
		String description = request.getParameter("description");
		String shortname = request.getParameter("shortname");
		String comments = "COMMENTO DI PROVA PER INIZIARE";
		// XXX promemoria: occhio a label in classifier e in dataset..

		
		
		// Then, retrieve resources: instance
		Instance vr_instance = checkFreeVRInstance();

		if (vr_instance == null) {
			System.out.println("[SubmitTrainJob] No vr_instances left to use!");
			JsonObject o = new JsonObject();
			o.addProperty("error", "no vr_instances left.");
			response.getWriter().println(o);
			return;
		}
		System.out.println("[SubmitTrainJob] Selected Instance with id: " + vr_instance.getId());

		// Then, retrieve resources: dataset
		Dataset dataset = retrieveTrainingSet(datasetId);

		if (dataset == null) {
			System.out.println("[SubmitTrainJob] No dataset found");

			JsonObject o = new JsonObject();
			o.addProperty("error", "no valid dataset found");
			response.getWriter().println(o);
			return;
		}

		System.out.println("[SubmitTrainJob] Selected Dataset with id: " + dataset.getId());

		// Valid dataset and valid instance: we can pass to daemon!
		ServletContext ctx = getServletContext();
		@SuppressWarnings("unchecked")
		JobQueue<Job<TrainingJobInfo>> trainQueue = (JobQueue<Job<TrainingJobInfo>>) ctx.getAttribute("trainQueue");

		trainQueue.addJob(new Job<TrainingJobInfo>(new TrainingJobInfo(dataset,label,description,shortname,comments)));

		// return answer
		System.out.println("[SubmitTrainJob] Passed training job to daemon, returning asnwer to client");

		JsonObject o = new JsonObject();
		o.addProperty("message", "Training job submitted for processing!");
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

	// retrieve datsaset to use for training
	private Dataset retrieveTrainingSet(String id) {
		Database db = CloudantClientMgr.getCloudantDB();

		// Condizione
		String selector = "{\"selector\": {\"_id\" : \"" + id + "\"}}";

		// Limita i campi
		FindByIndexOptions opt = new FindByIndexOptions().fields("_id").fields("label").fields("images");

		// execute query
		List<Dataset> datasets = db.findByIndex(selector, Dataset.class, opt);

		return datasets.isEmpty() ? null : datasets.get(0);
	}


	// Gets a free instance to use from cloudant
	private Instance checkFreeVRInstance() {

		Database db = CloudantClientMgr.getCloudantDB();

		// retrieve all available instances (empty classifiers)
		String selector = "{\"selector\": {\"type\": \"visual recognition instance\", \"classifiers\":[]}}";

		//System.out.println(selector);
		
		// Limita i campi
		FindByIndexOptions o = new FindByIndexOptions().fields("_id").fields("api_key");

		// execute query
		List<Instance> vr_instances = db.findByIndex(selector, Instance.class, o);

		if (vr_instances.isEmpty())
			return null;

		// select fist
		return vr_instances.get(0);
	}
}
