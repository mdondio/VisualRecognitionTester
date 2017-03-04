package net.mybluemix.visualrecognitiontester.servlet;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;

/**
 * This endpoint retrieves Room Status.
 * 
 * @author Marco Dondio
 */
@WebServlet("/GetRoomStatus")
public class GetRoomStatus extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetRoomStatus() {
		super();
	}

	/**
	 * @throws IOException
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		ServletContext ctx = getServletContext();

		JsonObject o = new JsonObject();
		o.addProperty("roomID", "marco_room");
		o.addProperty("roomLastScan", (String) ctx.getAttribute("roomLastScan"));
		o.addProperty("roomIsFree", (Boolean) ctx.getAttribute("roomIsFree"));

		response.getWriter().append(o.toString());
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
