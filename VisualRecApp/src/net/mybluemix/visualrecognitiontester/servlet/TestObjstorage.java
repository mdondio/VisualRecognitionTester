package net.mybluemix.visualrecognitiontester.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;

import net.mybluemix.visualrecognitiontester.blmxservices.ObjectStorage;
import net.mybluemix.visualrecognitiontester.blmxservices.ObjectStorageClient;

/**
 * Servlet implementation class TestObjstorage
 */
@WebServlet("/TestObjstorage")
public class TestObjstorage extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TestObjstorage() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// Retrieve our objectstorage
		ObjectStorage oo = ObjectStorageClient.getObjectStorage();
		
//		oo.createContainer("/nuovo2");

//		10118487447671458277
		
		// https://console.ng.bluemix.net/docs/services/ObjectStorage/os_authenticate.html
		// TODO capire errore 406
		System.out.println("-------------------");
//		oo.doGet("/", "");
//		System.out.println("-------------------");
//		oo.doGet("/prova", "");
//		System.out.println("-------------------");
//		oo.doGet("/prova", "/11865847224606702708.jpg");
//		
		
//		String json = oo.doGet("/", "");
	//	String json = oo.doGet(request.getParameter("containerName"), request.getParameter("objectName"));
		String json = oo.doGet(request.getParameter("containerName"), null);
//		response.getWriter().append("Served at: ").append(request.getContextPath());
//		response.getWriter().append(json).append(request.getContextPath());
		response.getWriter().append(json);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
