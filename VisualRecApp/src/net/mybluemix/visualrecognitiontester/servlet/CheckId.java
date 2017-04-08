package net.mybluemix.visualrecognitiontester.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cloudant.client.api.Database;

import net.mybluemix.visualrecognitiontester.blmxservices.CloudantClientMgr;

/**
 * This servlet will check cloudant DB for the presence of an id
 * @author Marco Dondio
 */
@WebServlet("/CheckId")
public class CheckId extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CheckId() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// Query cloudant to check whether this id exist
		String id = request.getParameter("id");

		Database db = CloudantClientMgr.getCloudantDB();

		response.getWriter().println(db.contains(id));
		
//		
//		if(db.contains(id))
//			response.getWriter().println(true);
//		else
//			response.getWriter().println(false);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		doGet(request, response);
	}
}
