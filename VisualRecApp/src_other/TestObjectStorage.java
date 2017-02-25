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
import services.ObjectStorageClient;

/**
 * Servlet implementation class TestDb
 */
@WebServlet("/TestObjectStorage")
public class TestObjectStorage extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TestObjectStorage() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		
//		db.fin
		System.out.println("[TestObjectStorage doGet()] - Function called");
		


		String userId = "77c4232dc11e4943955666a6b2523234";
		String password = "h{VlIFwaFu8&4pPx";
		String auth_url = "https://lon-identity.open.softlayer.com";	// qui occhio aggiungi /v3
		String domain = "1187923";
		String project = "object_storage_e0ebd231_88cf_4cfd_b548_ad95cb9a89e3";
		String region = "london";
		
		
		ObjectStorageClient oo = new ObjectStorageClient(userId, password, auth_url, domain, project, region);

		System.out.println(oo.doGet("provaContainer", "leandro.png"));

		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
