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
import net.mybluemix.visualrecognitiontester.backgroundaemons.datamodel.TrainingInfo;
import net.mybluemix.visualrecognitiontester.blmxservices.CloudantClientMgr;
import net.mybluemix.visualrecognitiontester.datamodel.Dataset;
import net.mybluemix.visualrecognitiontester.datamodel.Instance;

/**
 * This endpoint is used to submit a train job, analyze it and provide a
 * feedback to user. Actual job will be done asynchronously in background
 * 
 * @author Marco Dondio
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

		// First, parse args
		String datasetId = request.getParameter("datasetId");
		String label = request.getParameter("label");

		// XXX promemoria: occhio a label in classifier e in dataset..

		// Then, retrieve resources: instance
		Instance vr_instance = selectTargetInstance();

		if (vr_instance == null) {
			System.out.println("[SubmitTrainJob] No vr_instances left to use!");
			JsonObject o = new JsonObject();
			o.addProperty("error", "no vr_instances left.");
			response.getWriter().println(o);
			return;
		}
		System.out.println("[SubmitTrainJob] Selected Instance with apiKey: " + vr_instance.getApiKey());

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
		JobQueue<Job<TrainingInfo>> trainQueue = (JobQueue<Job<TrainingInfo>>) ctx.getAttribute("trainQueue");

		trainQueue.addJob(new Job<TrainingInfo>(new TrainingInfo(vr_instance, dataset, label)));

		// return answer
		System.out.println("[SubmitTrainJob] Passed training job to daemon, returning asnwer to client");

		JsonObject o = new JsonObject();
		o.addProperty("message", "Training job submitted for processing!");
		response.getWriter().println(o);

		return;
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

		// Ricevi get con parametro sub_type
		Database db = CloudantClientMgr.getCloudantDB();

		// Condizione
		String selector = "{\"selector\": {\"_id\" : \"" + id + "\"}}";

		// Limita i campi
		FindByIndexOptions opt = new FindByIndexOptions().fields("_id").fields("label").fields("images");

		// execute query
		List<Dataset> datasets = db.findByIndex(selector, Dataset.class, opt);

		return datasets == null ? null : datasets.get(0);
	}

	// Gets a free instance to use from cloudant
	private Instance selectTargetInstance() {

		Database db = CloudantClientMgr.getCloudantDB();

		// retrieve all available instances (empty classifiers)
		String selector = "{\"selector\": {\"type\":\": \"visual recognition instance\", \"classifiers\":[]}}";

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
