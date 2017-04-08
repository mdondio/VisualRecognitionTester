package net.mybluemix.visualrecognitiontester.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



/**
 * This endpoint is used to submit a dataset, analyze it and gives a feedback to
 * user. Actual job will be done asynchronously in background
 * 
 * @author Marco Dondio
 */
@WebServlet("/SubmitDataset")
public class SubmitDataset extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SubmitDataset() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		return;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// TODO
		// 0 - recupera tutte le immagini posotive e negative
		// 1 - processa tutte le img positive e negative
		// 2 - controllo di vario tipo, filtro
		// 3 - normalizza
		// 4 - produce gli zip

		// 5 - add to queue!
		// Now we add this classifier to the zombie queue...
//		ServletContext ctx = getServletContext();
//		@SuppressWarnings("unchecked")
//		JobQueue<Job<Classifier>> datasetQueue = (JobQueue<Job<Classifier>>) ctx.getAttribute("datasetQueue");
//		datasetQueue.addJob(new Job<Classifier>(classifierJson));

	}

}
