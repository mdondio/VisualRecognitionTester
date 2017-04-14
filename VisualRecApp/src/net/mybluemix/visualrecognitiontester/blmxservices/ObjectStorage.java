package net.mybluemix.visualrecognitiontester.blmxservices;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

// Vedere 
// https://sldn.softlayer.com/it/blog/waelriac/managing-softlayer-object-storage-through-rest-apis

// UTILE:
//http://jsonviewer.stack.hu

// http://developer.openstack.org/api-ref/object-storage

// Interessante: ma a noi conviene far passare tutto dal server, cosi c'è filtro login
// https://www.ibm.com/blogs/bluemix/2016/07/great-mobile-apps-with-object-storage-service/

public class ObjectStorage {

	private static final String AUTH_VERSION_1 = "/v1.0";
	private static final String AUTH_VERSION_2 = "/v2.0"; // -> unauthorized
															// (usavo dallas)
	private static final String AUTH_VERSION_3 = "/v3";
	// private static final String AUTH_VERSION_2 = "/v2.0"; // -> bad request

	private String userId = null;
	private String username = null;
	private String password = null;
	private String auth_url = null;// qui occhio aggiungi /v3
	//private String domain = null;
//	private String project = null; // penso sia anche il tenant
	private String projectId = null; // penso sia anche il tenant
//	private String region = null;

	private String storageUrl = null;
	
//	Use the value of the X-Subject-Token field from the response header as the X-Auth-Token field when you make requests to the Object Storage service.
	private String x_authToken = null;
	
		
	// TODO immagino in pratica lo stato che mantiene la sessione siano i
	// cookie?

	// TODO prevedi il caso in cui sia necessario un "relogin", il token scade..
	// controlla la risposta
	public ObjectStorage(String userId, String username, String password, String domain, String projectId,
			String project, String region, String auth_url) throws IOException {

		System.out.println("[ObjectStorage constructor] called");
		this.userId = userId;
		this.username = username;
		this.password = password;
		this.auth_url = auth_url;
		//this.domain = domain;
		this.projectId = projectId;
		//this.project = project;
		//this.region = region;

		this.storageUrl = ("london".equals(region) ? "https://lon." : "https://dal.") + "objectstorage.open.softlayer.com/v1/AUTH_" + projectId;
		// doAuthenticationV1();
		// doAuthenticationV2();
		doAuthenticationV3();
	}

	@SuppressWarnings("unused")
	@Deprecated
	// non esiste endpoint authv1 object storage SL?
	private void doAuthenticationV1() throws IOException {
		System.out.println("[ObjectStorage doAutenticationV1()] called");

		URL u = new URL(auth_url + AUTH_VERSION_1);

		HttpURLConnection con = (HttpURLConnection) u.openConnection();
		con.setRequestMethod("GET");

		// curl -i -H "X-Auth-User: <USERNAME>" -H "X-Auth-Key: <PASSWORD>"
		// <AUTH_URL>
		con.setRequestProperty("X-Auth-User", username);
		con.setRequestProperty("X-Auth-Key", password);

		int responseCode = con.getResponseCode();
		System.out.println("[ObjectStorage doAutenticationV1()] GET to: " + auth_url + AUTH_VERSION_1);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// print result
		System.out.println(response.toString());

	}

	// https://www.swiftstack.com/docs/cookbooks/swift_usage/auth.html
	// http://stackoverflow.com/questions/21404252/post-request-send-json-data-java-httpurlconnection
	// https://www.ibm.com/developerworks/community/blogs/8160eff1-dedf-408e-9395-9dffeb68749f/entry/accessing_openstack_using_rest_api1?lang=en
	@SuppressWarnings({ "unused", "unchecked" })
	@Deprecated
	private void doAuthenticationV2() throws IOException {

		System.out.println("[ObjectStorage doAutenticationV2()] called");

		URL u = new URL(auth_url + AUTH_VERSION_2 + "/tokens");

		HttpURLConnection con = (HttpURLConnection) u.openConnection();
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		// con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Accept", "application/json");
		con.setRequestMethod("POST");

		JSONObject cred = new JSONObject();
		JSONObject auth = new JSONObject();
		JSONObject parent = new JSONObject();

		cred.put("username", userId);
		cred.put("password", password);
		auth.put("tenantName", projectId); // XXX o project?
		auth.put("passwordCredentials", cred);
		parent.put("auth", auth);

		// TODO prova
		// https://ask.openstack.org/en/question/19863/authentication-keystone-httpurlconnection-java/

		// {
		// "auth" : {
		// "passwordCredentials" : {
		// "username" : "<username>",
		// "password" : "<password>"
		// },
		// "tenantName" : "<tenant>"
		// }
		// }

		// da forum, per bluemix dice che va
		// --os-auth-url <auth_url>/v2.0
		// --os-storage-url <swift_url>
		// --os-region-name <region>
		// --os-tenant-name <project>
		// --os-username <credentials.userid>
		// --os-password <credentials.password> -V 2 list

		System.out.println("[ObjectStorage doAutenticationV2()] POST to: " + auth_url + AUTH_VERSION_2);
		System.out.println(parent);

		OutputStream os = con.getOutputStream();
		os.write(parent.toString().getBytes("UTF-8"));
		os.close();

		// display what returns the POST request

		StringBuilder sb = new StringBuilder();
		int HttpResult = con.getResponseCode();
		if (HttpResult == HttpURLConnection.HTTP_OK) {
			BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			br.close();
			System.out.println("" + sb.toString());
		} else {
			System.out.println(con.getResponseMessage());
		}

	}

	// http://developer.openstack.org/api-ref/identity/v3/index.html
	// http://developer.openstack.org/api-ref/identity/v3/index.html?expanded=password-authentication-with-unscoped-authorization-detail
	// https://www.ibm.com/developerworks/community/blogs/1b48459f-4091-43cb-bca4-37863606d989?lang=en
	// /v3/auth/tokens
	// Password authentication with scoped authorization
	@SuppressWarnings("unchecked")
	private void doAuthenticationV3() throws IOException {

		URL u = new URL(auth_url + AUTH_VERSION_3 + "/auth/tokens");

		HttpURLConnection con = (HttpURLConnection) u.openConnection();
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		con.setRequestMethod("POST");

		// Identity section
		JSONObject identity = new JSONObject();
		JSONArray methods = new JSONArray();
		methods.add("password");
		identity.put("methods", methods);

		JSONObject passBlock = new JSONObject();
		JSONObject userBlock = new JSONObject();
		userBlock.put("id", userId);
		userBlock.put("password", password);
		passBlock.put("user", userBlock);
		identity.put("password", passBlock);

		// Scope section
		JSONObject scope = new JSONObject();
		JSONObject project = new JSONObject();
		project.put("id", projectId);
		scope.put("project", project);

		// Build auth JSON
		JSONObject auth = new JSONObject();
		auth.put("identity", identity);
		auth.put("scope", scope);

		// Build final JSON
		JSONObject parent = new JSONObject();
		parent.put("auth", auth);

		System.out.println("[ObjectStorage doAutenticationV3()] POST to: " + auth_url + AUTH_VERSION_3);
		System.out.println(parent);

		OutputStream os = con.getOutputStream();
		os.write(parent.toString().getBytes("UTF-8"));
		os.close();

		 x_authToken = con.getHeaderField("X-Subject-Token");
		System.out.println("X-Subject-Token: " + x_authToken);
		
//		Use the value of the X-Subject-Token field from the response header as the X-Auth-Token field when you make requests to the Object Storage service.
		
		// display what returns the POST request
		StringBuilder sb = new StringBuilder();
		//int HttpResult = con.getResponseCode();
		BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
		String line = null;
		while ((line = br.readLine()) != null) {
			sb.append(line + "\n");
		}
		br.close();
		System.out.println("" + sb.toString());
	}

	public HttpURLConnection doGet(String containerName, String objectName) throws IOException {
//		Use the value of the X-Subject-Token field from the response header as the X-Auth-Token field when you make requests to the Object Storage service.

		System.out.println("[ObjectStorage doGet()] Retrieving: "+ storageUrl + containerName + objectName);

		URL u = new URL(storageUrl + containerName + objectName);

		HttpURLConnection con = (HttpURLConnection) u.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("X-Auth-Token", x_authToken);
		con.setRequestProperty("Accept", "application/json");

		
		// If token expired, 401 error
		if(con.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED){
			doAuthenticationV3();
			
			// do get again with new token
			return doGet(containerName, objectName);
		}
		
		// token ok, return con
		return con;
		/*
		// XXX
		// Se ho un 404, la riga sotto da eccezione filenotfound
		int responseCode = con.getResponseCode();
		System.out.println("Response Code : " + responseCode);
//
		
		System.out.println("Content-Length: " + con.getHeaderField("Content-Length"));
		System.out.println("Content-Type: " + con.getHeaderField("Content-Type"));

		
		
		// Se ho un 404, la riga sotto da eccezione filenotfound
		
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// print result
		System.out.println(response.toString());
//
//		*/
//		
//		
	}

	public void doPost() {
		System.out.println("[ObjectStorage doPost()] called");
		// TODO

	}
	
	
	// https://www.mkyong.com/java/how-to-convert-byte-to-bufferedimage-in-java/
	public void doPut(String containerName, String objectName, BufferedImage image) throws IOException{
	// https://developer.openstack.org/api-ref/object-storage/index.html?expanded=create-or-update-object-metadata-detail,create-or-replace-object-detail#objects	
//		curl -i $publicURL/janeausten/helloworld.txt -X PUT -d "Hello" -H "Content-Type: text/html; charset=UTF-8" -H "X-Auth-Token: $token"


		System.out.println("[ObjectStorage doPut()] Retrieving: "+ storageUrl + containerName + objectName);

		URL u = new URL(storageUrl + containerName + objectName);

		HttpURLConnection con = (HttpURLConnection) u.openConnection();

		con.setRequestMethod("PUT");
		con.setRequestProperty("X-Auth-Token", x_authToken);
		con.setDoOutput(true);

		
		OutputStream out = con.getOutputStream();
		ImageIO.write(image, "jpg", out);
		out.close();
		

//		// If token expired, 401 error
		//	con.getInputStream();
		if(con.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED){
			doAuthenticationV3();
//			
//			// do Put again with new token
			 doPut(containerName, objectName, image);
		}
	}
	

	public void doDelete(String containerName, String objectName) throws IOException {
		System.out.println("[ObjectStorage doDelete()] called");
		
		
		URL u = new URL(storageUrl + containerName + objectName);

		HttpURLConnection con = (HttpURLConnection) u.openConnection();

		con.setRequestMethod("DELETE");
		con.setRequestProperty("X-Auth-Token", x_authToken);
		con.setDoOutput(true);


//		// If token expired, 401 error
		//	con.getInputStream();
		if(con.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED){
			doAuthenticationV3();
//			
//			// do Put again with new token
			 doDelete(containerName, objectName);
		}
		
		
		
		

	}


	// http://developer.openstack.org/api-ref/object-storage/?expanded=list-activated-capabilities-detail,show-container-details-and-list-objects-detail,show-account-metadata-detail,create-container-detail
public void createContainer(String containerName) throws IOException{

	System.out.println("[ObjectStorage createContainer()] Creating...");
	System.out.println(storageUrl);
	

	URL u = new URL(storageUrl);

	HttpURLConnection con = (HttpURLConnection) u.openConnection();
	con.setRequestMethod("PUT");
	con.setRequestProperty("X-Auth-Token", x_authToken);
	con.setRequestProperty("Accept", "application/json");


	// XXX
	// Se ho un 404, la riga sotto da eccezione filenotfound
	int responseCode = con.getResponseCode();
	System.out.println("Response Code : " + responseCode);

	// 201 vuol dire successo, creato correttamente
	// XXX 202 vuol dire che già esisteva.. capire cosa fa
	
	
	// Se ho un 404, la riga sotto da eccezione filenotfound
//	
//	BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//	String inputLine;
//	StringBuffer response = new StringBuffer();
//
//	while ((inputLine = in.readLine()) != null) {
//		response.append(inputLine);
//	}
//	in.close();
//
//	// print result
//	System.out.println(response.toString());

}
	
}