package myservlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cloudant.client.api.Database;

import services.CloudantClientMgr;

/**
 * Servlet implementation class TestDb
 */
@WebServlet("/TestDb")
public class TestDb extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TestDb() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		
//		db.fin
		System.out.println("[TestDb doGet()] - Function called");
		


		 //----------------
		// da qui test list

		 int readBufferSize = 8192;
		// TODO remove me
		Database db = CloudantClientMgr.getDB();

		
		// Get an ExampleDocument out of the database and deserialize the JSON into a Java type
//		ExampleDocument doc = db.find(ExampleDocument.class,"example_id");
		
		
//		System.out.println(doc);
//		
//		https://github.com/cloudant/java-cloudant#getting-started
		
		InputStream dbResponse = db.find("a40fce6329c185129d0d6ac72f4a4b22d23ffba1-watch_classifier_195755765");
		OutputStream output = response.getOutputStream();

		try {
			int readBytes = 0;
			byte[] buffer = new byte[readBufferSize];
			while ((readBytes = dbResponse.read(buffer)) >= 0) {
				output.write(buffer, 0, readBytes);
			}
		} finally {
			dbResponse.close();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
