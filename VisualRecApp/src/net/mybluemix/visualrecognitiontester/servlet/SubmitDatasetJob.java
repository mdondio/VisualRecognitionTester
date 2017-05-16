package net.mybluemix.visualrecognitiontester.servlet;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.cloudant.client.api.Database;
import com.google.gson.JsonObject;

import net.mybluemix.visualrecognitiontester.backgroundaemons.BackgroundDaemonManager;
import net.mybluemix.visualrecognitiontester.backgroundaemons.datamodel.DatasetJobInfo;
import net.mybluemix.visualrecognitiontester.backgroundaemons.datamodel.Job;
import net.mybluemix.visualrecognitiontester.backgroundaemons.datamodel.JobQueue;
import net.mybluemix.visualrecognitiontester.backgroundaemons.datamodel.DatasetJobInfo.TYPE;
import net.mybluemix.visualrecognitiontester.blmxservices.CloudantClientMgr;
import net.mybluemix.visualrecognitiontester.blmxservices.marcovisualreclibrary.exceptions.VisualClassifierException;

/**
 * This endpoint is used to submit dataset job. A job can be insert or delete,
 * and it will specified by "type" parameter
 * 
 * 
 * user. Actual job will be done asynchronously in background
 * 
 * @author Marco Dondio
 */
// http://balusc.omnifaces.org/2009/12/uploading-files-in-servlet-30.html

// https://css-tricks.com/snippets/html/multiple-file-input/
// http://docs.oracle.com/javaee/6/tutorial/doc/glraq.html
// https://ursaj.com/upload-files-in-java-with-servlet-api
// http://stackoverflow.com/questions/3337056/convenient-way-to-parse-incoming-multipart-form-data-parameters-in-a-servlet

// TODO punto di grossa attenzione: questo metodo è molto pesante per il
// server.. confido che non venga usato massivamente contemporanemente..
// altrimenti
// dovrei studiare alternative scrivendo in una "area di staging" sul disco

@WebServlet("/SubmitDatasetJob")
@MultipartConfig
public class SubmitDatasetJob extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.

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

		// First parse request
		DatasetJobInfo dji = null;
		File datasetStagingArea = null;
		try {

			// Create a random directory
			// XXX make more robust
			datasetStagingArea = new File(BackgroundDaemonManager.STAGING_AREA + "/" + System.currentTimeMillis() + "_"
					+ request.getContentLength());

			// Should never happen...
			if (datasetStagingArea.exists()) {
				JsonObject o = new JsonObject();
				o.addProperty("error", "SubmitDatasetJob Server error, please retry.");
				response.getWriter().println(o);
				return;
			}

			// Create staging area
			datasetStagingArea.mkdir();

			dji = parseRequest(datasetStagingArea, request, response);
		} catch (VisualClassifierException e) {

			// Delete our staging area!
			if (datasetStagingArea.exists())
				deleteStagingArea(datasetStagingArea);

			JsonObject o = new JsonObject();
			o.addProperty("error", "SubmitDatasetJob invalid request: " + e.getMessage());
			response.getWriter().println(o);
			return;
		}

		// Valid request, pass to daemon
		ServletContext ctx = getServletContext();
		@SuppressWarnings("unchecked")
		JobQueue<Job<DatasetJobInfo>> datasetQueue = (JobQueue<Job<DatasetJobInfo>>) ctx.getAttribute("datasetQueue");
		datasetQueue.addJob(new Job<DatasetJobInfo>(dji));

		// return answer to client
		System.out.println("[SubmitDatasetJob doPost()] Passed dataset job to daemon, returning answer to client");

		JsonObject o = new JsonObject();
		o.addProperty("message", "DatasetJob submitted for processing!");
		response.getWriter().println(o);
	}

	private void deleteStagingArea(File datasetStagingArea) {
		
		System.out.println("[SubmitTrainJob deleteStagingArea()] Deleting " + datasetStagingArea);

		for (File images : datasetStagingArea.listFiles())
			images.delete();
		datasetStagingArea.delete();
	}

	/**
	 * This method builds a valid datasetjob info by parsing a valid request.
	 * Returns null if request is not valid.
	 * 
	 * @param datasetStagingArea
	 */
	private DatasetJobInfo parseRequest(File datasetStagingArea, HttpServletRequest req, HttpServletResponse response)
			throws VisualClassifierException, IOException, ServletException {

		DatasetJobInfo dji = null;
		int posCounter = 0;
		int negCounter = 0;
		
		HashMap<String, String> textParameters = new HashMap<String, String>();

		// parse all request
		for (Part part : req.getParts()) {
			String name = part.getName(); // positive[] | negative[]
			String contentType = part.getContentType();
			long fileSize = part.getSize();

			// text parameter
			if (contentType == null) {
				String value = getValue(part.getInputStream());
				System.out.println(
						"[SubmitDataset parseRequest()] Parsing valid text part, name: " + name + ", value: " + value);
				textParameters.put(name, value);
				continue;
			}
			// valid image: jpg or png
			else if (contentType.equals("image/jpeg") || contentType.equals("image/jpg")
					|| contentType.equals("image/png") || contentType.equals("image/x-png")) {

				System.out.println("[SubmitDataset parseRequest()] Parsing image part, name: " + name
						+ " content-type: " + contentType + " filesize: " + fileSize);

				// Now extract image
				BufferedImage img = extractImage(part);

				// If png, convert to jpg
				if (contentType.contains("png")) {
					System.out.println("[SubmitDataset parseRequest()] Png image, converting to jpg");
					img = convertPngToJpg(img);
				}


				// Now check name (class)
				System.out.println("[SubmitDataset parseRequest()] Writing image sto staging area...");

				if (name.equals("positives[]")) 
					ImageIO.write(img, "jpg", new File(datasetStagingArea.getAbsolutePath() + "/pos_" + posCounter++ + ".jpg"));
				 else if (name.equals("negatives[]")) 
					ImageIO.write(img, "jpg", new File(datasetStagingArea.getAbsolutePath() + "/neg_" + negCounter++ + ".jpg"));
				
				continue;
			} else {
				System.out.println("[SubmitDataset parseRequest()] Invalid part, skip. content-type: " + contentType);
				continue;
			}
		}

		// Finally check if we have valid request
		String type = textParameters.get("type");
		String datasetId = textParameters.get("datasetId");
		String label = textParameters.get("label");

		// Some checks
		if (datasetId == null || type == null || datasetId.isEmpty() || type.isEmpty()
				|| (!type.equals("insert") && !type.equals("delete"))) {
			System.out.println("[SubmitDataset parseRequest()] Invalid request parameter, type: " + type
					+ " datasetId: " + datasetId);

			throw new VisualClassifierException(
					"Invalid request parameter, type: " + type + " datasetId: " + datasetId);
		}

		// Valid request! Prepare the job info
		if (type.equals("insert")) {

			if ((posCounter + negCounter) == 0) {

				System.out.println("[SubmitDataset parseRequest()] Insert has zero images. Skip.");
				throw new VisualClassifierException("Insert has zero valid images");
			}

			// Query cloudant to check whether this id exist

			Database db = CloudantClientMgr.getCloudantDB();

			if (db.contains(datasetId)) {

				System.out.println("[SubmitDataset parseRequest()] Id already used, skip. DatasetId: " + datasetId);
				throw new VisualClassifierException("datasetId already used.");
			}

			System.out.println("[SubmitDataset parseRequest()] Valid insert request!");

			dji = new DatasetJobInfo(datasetId, TYPE.INSERT);
			dji.setLabel(label);
			dji.setStagingFolder(datasetStagingArea);
			dji.setPosCounter(posCounter);
			dji.setNegCounter(negCounter);
			
		} else {
			System.out.println("[SubmitDataset parseRequest()] Valid delete request!");
			dji = new DatasetJobInfo(datasetId, TYPE.DELETE);
		}

		return dji;
	}

	/**
	 * Returns the text value of the given part.
	 */
	private String getValue(InputStream input) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));

		StringBuilder value = new StringBuilder();
		char[] buffer = new char[DEFAULT_BUFFER_SIZE];
		for (int length = 0; (length = reader.read(buffer)) > 0;) {
			value.append(buffer, 0, length);
		}
		return value.toString();
	}

	// Retrieves binary data from image Part
	private BufferedImage extractImage(Part imagePart) throws IOException {

		return ImageIO.read(imagePart.getInputStream());

	}

	// converts an image to jpg
	private BufferedImage convertPngToJpg(BufferedImage pngImage) throws IOException {

		// try {

		// create a blank, RGB, same width and height, and a white background
		BufferedImage jpgImage = new BufferedImage(pngImage.getWidth(), pngImage.getHeight(),
				BufferedImage.TYPE_INT_RGB);
		jpgImage.createGraphics().drawImage(pngImage, 0, 0, Color.WHITE, null);

		return jpgImage;
		// write to jpeg file
		// ImageIO.write(newBufferedImage, "jpg", new File("output.jpg"));

		// System.out.println("Done");

		// } catch (IOException e) {
		//
		// e.printStackTrace();
		//
		// }
		//

	}
}
