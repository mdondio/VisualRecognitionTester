package net.mybluemix.visualrecognitiontester.servlet;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.mybluemix.visualrecognitiontester.backgroundaemons.JobQueue;

/**
 * Servlet implementation class SubmitJob
 */
@WebServlet("/SubmitJob")
public class SubmitJob extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SubmitJob() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Add a job to queue
		ServletContext ctx = getServletContext();

		JobQueue mqttQueue = (JobQueue) ctx.getAttribute("jobQueue");
		Long id = Math.round(Math.random() * 100);
		mqttQueue.addJob(id);

		// Get job counter
		Integer jobCounter = (Integer) ctx.getAttribute("jobCounter");
		response.getWriter().append("Submitted a new job (id = " + id + "). Job executed so far: " + jobCounter);
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
