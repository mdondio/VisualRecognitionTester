package net.mybluemix.visualrecognitiontester.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.google.gson.JsonObject;

import net.mybluemix.visualrecognitiontester.backgroundaemons.datamodel.DatasetJobInfo;
import net.mybluemix.visualrecognitiontester.backgroundaemons.datamodel.Job;
import net.mybluemix.visualrecognitiontester.backgroundaemons.datamodel.JobQueue;
import net.mybluemix.visualrecognitiontester.backgroundaemons.datamodel.DatasetJobInfo.TYPE;

/**
 * This endpoint is used to submit dataset job. A job can be insert or delete,
 * and it will specified by "type" parameter
 * 
 * 
 * user. Actual job will be done asynchronously in background
 * 
 * @author Marco Dondio
 */
// https://css-tricks.com/snippets/html/multiple-file-input/
// http://docs.oracle.com/javaee/6/tutorial/doc/glraq.html

@WebServlet("/SubmitDatasetJob")
@MultipartConfig
public class SubmitDatasetJob extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SubmitDatasetJob() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// doPost(request, response);
		return;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// insert o delete
		String type = request.getParameter("type");
		String datasetId = request.getParameter("datasetId");

		////////////////////////////
		// XXX remove me
		datasetId = "prova";
		type = "insert";
		////////////////////////////

		// prepare datasetjob info
		DatasetJobInfo dji = null;

		if (type.equals("insert")) {

			System.out.println("[SubmitDatasetJob doPost()] Insert, parsing positive and negative.");

			// Prepare the job info
			dji = new DatasetJobInfo(datasetId, TYPE.INSERT);

			// Extract images and add to dji
			parseRequest(request, dji);
		}

		else if (type.equals("delete")) {

			System.out.println("[SubmitDatasetJob doPost()] Delete, parsing positive and negative.");

			dji = new DatasetJobInfo(datasetId, TYPE.DELETE);

			System.out.println("[SubmitDatasetJob doPost()] Delete");
		} else {
			JsonObject o = new JsonObject();
			o.addProperty("error", "SubmitDatasetJob expects type insert or delete!");
			response.getWriter().println(o);
			return;
		}

		// Finally prepare the job info and send to queue
		ServletContext ctx = getServletContext();
		@SuppressWarnings("unchecked")
		JobQueue<Job<DatasetJobInfo>> datasetQueue = (JobQueue<Job<DatasetJobInfo>>) ctx.getAttribute("datasetQueue");

		datasetQueue.addJob(new Job<DatasetJobInfo>(dji));

		// return answer to client
		System.out.println("[SubmitTrainJob] Passed dataset job to daemon, returning answer to client");

		JsonObject o = new JsonObject();
		o.addProperty("message", "DatasetJob submitted for processing!");
		response.getWriter().println(o);

	}

	// https://ursaj.com/upload-files-in-java-with-servlet-api
	private void parseRequest(HttpServletRequest req, DatasetJobInfo dji) throws IOException, ServletException {

		List<Part> positives = new ArrayList<Part>();
		List<Part> negatives = new ArrayList<Part>();

		for (Part part : req.getParts()) {
			long fileSize = part.getSize();
			String name = part.getName();
			String imageClass = part.getHeader("class"); // positive | negative

			String contentDisposition = part.getHeader("content-disposition");
			String contentType = part.getContentType();

			System.out.println("[SubmitDataset parseRequest()] Received: " + name + " - (" + contentDisposition + ") - "
					+ contentType + " - " + fileSize);

			// TODO generalizzare
			if (!contentType.equals("image/jpeg")) {
				System.out.println("[SubmitDataset parseRequest()] Skip part: content-type: " + contentType);
				continue;
			}

			if (imageClass.equals("positive")) {
				positives.add(part);
			} else if (imageClass.equals("negative")) {
				negatives.add(part);
			}
		}

		// Add to DatasetJobInfo
		dji.setPositives(positives);
		dji.setNegatives(negatives);
	}

	// http://stackoverflow.com/questions/25669874/opening-an-image-file-from-java-inputstream
	// private void buildImage(int i, InputStream imageStream){
	//
	// System.out.println("[SubmitDataset buildImage()] start");
	//
	// try (FileOutputStream out = new FileOutputStream(new
	// File(getServletContext().getRealPath("/")+"test_"+i+".jpg"))) {
	// byte[] buffer = new byte[1024];
	// int len;
	// while ((len = imageStream.read(buffer)) != -1) {
	//
	// out.write(buffer, 0, len);
	// System.out.println(buffer);
	//
	// }
	// System.out.println("[SubmitDataset buildImage()] close");
	//
	// out.close();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

}
