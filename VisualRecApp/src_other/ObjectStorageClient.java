package services;



	import java.io.IOException;
	import java.io.InputStream;

import org.openstack4j.api.OSClient;
import org.openstack4j.api.storage.ObjectStorageService;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.common.DLPayload;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.model.common.Payload;
import org.openstack4j.model.storage.object.SwiftAccount;
import org.openstack4j.model.storage.object.SwiftContainer;
import org.openstack4j.model.storage.object.SwiftObject;
import org.openstack4j.openstack.OSFactory;

//	import javax.servlet.ServletException;
//	import javax.servlet.annotation.WebServlet;
//	import javax.servlet.http.HttpServlet;
//	import javax.servlet.http.HttpServletRequest;
//	import javax.servlet.http.HttpServletResponse;
//
//	import org.apache.commons.io.IOUtils;
//	import org.openstack4j.api.OSClient.OSClientV3;
//	import org.openstack4j.api.storage.ObjectStorageService;
//	import org.openstack4j.model.common.ActionResponse;
//	import org.openstack4j.model.common.DLPayload;
//	import org.openstack4j.model.common.Identifier;
//	import org.openstack4j.model.common.Payload;
//	import org.openstack4j.model.storage.object.SwiftObject;
//	import org.openstack4j.openstack.OSFactory;


/**
 * Based on
 * https://github.com/ibm-bluemix-mobile-services/bluemix-objectstorage-sample-liberty/blob/master/src/main/java/wasdev/sample/servlet/SimpleServlet.java
 * @author Marco Dondio
 *
 */
	public class ObjectStorageClient {

		//Get these credentials from Bluemix by going to your Object Storage service, and clicking on Service Credentials:
//		private static final String USERNAME = "PUT_YOUR_OBJECT_STORAGE_USERNAME_HERE";
//		private static final String PASSWORD = "PUT_YOUR_OBJECT_STORAGE_PASSWORD_HERE";
//		private static final String DOMAIN_ID = "PUT_YOUR_OBJECT_STORAGE_DOMAIN_ID_HERE";
//		private static final String PROJECT_ID = "PUT_YOUR_OBJECT_STORAGE_PROJECT_ID_HERE";

		private ObjectStorageService objectStorage;
		
		
		
//		
//		 "Object-Storage": [
//		                    {
//		                        "credentials": {
//		                            "auth_url": "https://lon-identity.open.softlayer.com",
//		                            "project": "object_storage_e0ebd231_88cf_4cfd_b548_ad95cb9a89e3",
//		                            "projectId": "b8240e4d9c1449a6ac7ae6159b116282",
//		                            "region": "london",
//		                            "userId": "77c4232dc11e4943955666a6b2523234",
//		                            "username": "admin_2f29fbd3176b0f3992922a52cd5f8b330fbde111",
//		                            "password": "h{VlIFwaFu8&4pPx",
//		                            "domainId": "d7b14e49480942beaade5d2cce531404",
//		                            "domainName": "1187923",
//		                            "role": "admin"

		
//		Open a new Softlayer Connection in File,
//		Display Name : choose a name,
//		User Name : Username from the objectstorage credentials in Bluemix ,
//		Api Key : Password from the objectstorage in Bluemix credentials
//		Authentication Service : empty for the moment
//		Account Location : name of the city (Dallas or London)
//		Keystone version : 3 (this is for Bluemix),
//		Domain Name : Domainname from the objectstorage credentials in Bluemix, 

		public ObjectStorageClient(String userId, String password, String auth_url, String domain, String project, String region){
			
			// TODO
			
//			
			 Identifier domainIdent = Identifier.byName(domain);
			 Identifier projectIdent = Identifier.byName(project);
			 
				System.out.println("Authenticating...");

//				
				OSClient<?> os = OSFactory.builderV3()
						 .endpoint(auth_url + "/v3")	// XXX attenzione al /v3 !!!
						 .credentials(userId, password)
						 .scopeToProject(projectIdent, domainIdent)
						 .authenticate();

				// Switch to region
				  os.useRegion(region);
				
				
//				OSClientV3 os = OSFactory.builderV3()
//				.endpoint(auth_url + "/v3")// XXX attenzione al /v3 !!!
//				.credentials(userId,password, domainIdent)
//				.scopeToProject(projectIdent)
//				.authenticate();


				System.out.println("Authenticated successfully!");
				objectStorage= os.objectStorage();
				
				SwiftAccount account = objectStorage.account().get();
				
				System.out.println("Account: \n" + account);

				System.out.println("Containers:");
				
				for(SwiftContainer sc : objectStorage.containers().list()){
					System.out.println(sc.getName() + " -> object count = " + sc.getObjectCount() + " size = " + sc.getTotalSize());
					System.out.println(sc.getMetadata());
				}
				

			 
//			objectStorage = authenticateAndGetObjectStorageService();
		}
		


			////////////////
			// original
//			OSClientV3 os = OSFactory.builderV3()
//					.endpoint(OBJECT_STORAGE_AUTH_URL)
//					.credentials(USERNAME,PASSWORD, domainIdentifier)
//					.scopeToProject(Identifier.byId(PROJECT_ID))
//					.authenticate();
//
//			System.out.println("Authenticated successfully!");
//
//			objectStorage = os.objectStorage();

			////////////////
			

		
		

		// Read object
		public SwiftObject doGet(String containerName, String fileName) {

			System.out.println("Retrieving file from ObjectStorage...");

			if(containerName == null || fileName == null){ //No file was specified to be found, or container name is missing
				System.out.println("Container name or file name was not specified.");
				return null;
			}

			SwiftObject pictureObj = objectStorage.objects().get(containerName,fileName);

			if(pictureObj == null){ //The specified file was not found
				System.out.println("File not found.");
				return null;
			}

			String mimeType = pictureObj.getMimeType();

			DLPayload payload = pictureObj.download();

			InputStream in = payload.getInputStream();


			System.out.println("Successfully retrieved file from ObjectStorage!");
			return pictureObj;
		}

		// Add / update object
		// returns null if fail
		public String doPost(String containerName, String fileName, InputStream fileStream) {

			System.out.println("Storing file in ObjectStorage...");


			if(containerName == null || fileName == null){ //No file was specified to be found, or container name is missing
				System.out.println("File not found.");
				return null;
			}
			
			
			Payload<InputStream> payload = new PayloadClass(fileStream);
			
			// XXX da capire: ritorna null se non funziona, altrimenti un qualceh ID
			return objectStorage.objects().put(containerName, fileName, payload);
		}

		private class PayloadClass implements Payload<InputStream> {
			private InputStream stream = null;

			public PayloadClass(InputStream stream) {
				this.stream = stream;
			}

			@Override
			public void close() throws IOException {
				stream.close();
			}

			@Override
			public InputStream open() {
				return stream;
			}

			@Override
			public void closeQuietly() {
				try {
					stream.close();
				} catch (IOException e) {
				}
			}

			@Override
			public InputStream getRaw() {
				return stream;
			}

		}

		// Delete obj
		public ActionResponse doDelete(String containerName, String fileName){

			System.out.println("Deleting file from ObjectStorage...");


			if(containerName == null || fileName == null){ //No file was specified to be found, or container name is missing
				System.out.println("File not found.");
				return null;
			}

			ActionResponse deleteResponse = objectStorage.objects().delete(containerName,fileName);

			if(!deleteResponse.isSuccess()){
				System.out.println("Delete failed: " + deleteResponse.getFault());
				return null;
			}

			System.out.println("Successfully deleted file from ObjectStorage!");
			return deleteResponse;
		}
		

		public void listContainers(){
			
			for(SwiftContainer sc : objectStorage.containers().list())
				System.out.println(sc.getName() + " object count -> " + sc.getObjectCount());
			
			
		}

	
}
