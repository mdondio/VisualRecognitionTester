package net.mybluemix.visualrecognitiontester.servlet;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.mybluemix.visualrecognitiontester.backgroundaemons.Job;
import net.mybluemix.visualrecognitiontester.backgroundaemons.JobQueue;
import net.mybluemix.visualrecognitiontester.datamodel.Classifier;


/**
 * This endpoint is used to submit a train job, analyze it and provide a feedback to
 * user. Actual job will be done asynchronously in background
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
		return;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

return;
	}

}
