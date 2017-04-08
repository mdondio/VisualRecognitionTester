package net.mybluemix.visualrecognitiontester.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.mybluemix.visualrecognitiontester.blmxservices.Configs;
import net.mybluemix.visualrecognitiontester.blmxservices.ObjectStorage;
import net.mybluemix.visualrecognitiontester.blmxservices.ObjectStorageClientMgr;

/**
 * This servlet will retrieve an image from object storage
 */
@WebServlet("/GetImage")
public class GetImage extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetImage() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	// Vedere
	// http://stackoverflow.com/questions/2979758/writing-image-to-servlet-response-with-best-performance
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {

		System.out.println("[GetImage doGet()] Function called");

		String imageID = request.getParameter("image_id");
		if (imageID == null) {
			System.out.println("[GetImage doGet()]: no args");

			return;
		}

		// Get oo instance
		ObjectStorage oo = null;
		try {
			oo = ObjectStorageClientMgr.getObjectStorage();
		} catch (IOException e1) {
			System.out.println("[GetImage doGet()]: error while retrieving object storage");
			return;
		}

		// Get image
		HttpURLConnection con = null;
		BufferedInputStream imageStream = null;
		BufferedOutputStream output = null;

		try {
			con = oo.doGet("/" + Configs.OO_DEFAULTCONTAINER, "/" + imageID + ".jpg");
			imageStream = new BufferedInputStream(con.getInputStream(), 2048);

			// skip non images
			if (!con.getHeaderField("Content-Type").equals("image/jpeg")) {
				System.out.println("[GetImage doGet()]: error while retrieving " + imageID + ".jpg.");
				imageStream.close();
				return;
			}

			// If we are here we retrieved image 
			// correctly. Generate output
			response.setHeader("Content-Type", "image/jpeg");


			output = new BufferedOutputStream(response.getOutputStream());
			byte[] buffer = new byte[8192];
			for (int length = 0; (length = imageStream.read(buffer)) > 0;)
				output.write(buffer, 0, length);

		} catch (Exception e) {

			System.out.println("[GetImage doGet()]: error while retrieving " + imageID + ".jpg.");
			return;
		} finally {
			if (imageStream != null)
				try {
					imageStream.close();
				} catch (IOException e) {}

			if (output != null)
				try {
					output.close();
				} catch (IOException e) {}
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
