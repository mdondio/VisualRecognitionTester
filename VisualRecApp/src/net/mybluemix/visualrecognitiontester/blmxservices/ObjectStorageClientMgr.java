package net.mybluemix.visualrecognitiontester.blmxservices;

import java.io.IOException;

import java.util.Date;
import java.util.Map.Entry;
import java.util.Set;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.org.lightcouch.CouchDbException;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.mybluemix.visualrecognitiontester.datamodel.DateAdapter;
import net.mybluemix.visualrecognitiontester.datamodel.Images;
import net.mybluemix.visualrecognitiontester.datamodel.ImagesAdapter;

/**
 * This class is needed to manage the ObjectStorage service instance. This class
 * guarantees we will instantiate only one instance of the service.
 * 
 * @author Marco Dondio
 *
 */
// TODO: implementare VCAP_SERVICES quando eseguita dentro Bluemix
public class ObjectStorageClientMgr {

	private static ObjectStorage oo = null;

	
	// This method guarantees we will have only one instance of this object
	public static ObjectStorage getObjectStorage() throws IOException {

		if (oo == null) {
			System.out.println("[getObjectStorage()] ObjectStorage is null: need to initialize");
			initClient();
		} else {
			System.out.println("[getObjectStorage()] ObjectStorage already instantiated: retrieving");

		}

		return oo;

	}
	private static void initClient() throws IOException {

		synchronized (ObjectStorageClientMgr.class) {
			if (oo != null)
				return;

			oo = createObjectStorage();

		} // end synchronized
	}

	private static ObjectStorage createObjectStorage() throws IOException {

		String VCAP_SERVICES = System.getenv("VCAP_SERVICES");
		String serviceName = null;
		
		String userId = null;
		String username = null;
		String password = null;
		String auth_url = null;// qui occhio aggiungi /v3
		String domain = null;
		String project = null;
		String projectId = null;
		String region = null;

		if (VCAP_SERVICES != null) {
			// When running in Bluemix, the VCAP_SERVICES env var will have the
			// credentials for all bound/connected services
			// Parse the VCAP JSON structure looking for cloudant.	
			JsonObject obj = (JsonObject) new JsonParser().parse(VCAP_SERVICES);
			Entry<String, JsonElement> dbEntry = null;
			Set<Entry<String, JsonElement>> entries = obj.entrySet();
			// Look for the VCAP key that holds the cloudant no sql db
			// information
			for (Entry<String, JsonElement> eachEntry : entries) {
			if (eachEntry.getKey().toLowerCase().contains("object-storage")) {
			dbEntry = eachEntry;
			break;
			}
			}
			if (dbEntry == null) {
				throw new RuntimeException("Could not find Object Storage key in VCAP_SERVICES env variable");
			}

			// ---- altro esempio json
			 obj = (JsonObject) ((JsonArray) dbEntry.getValue()).get(0);
			 serviceName = (String) dbEntry.getKey();
			 System.out.println("Service Name - " + serviceName);
			
			 obj = (JsonObject) obj.get("credentials");
			
			 userId = obj.get("userId").getAsString();
			 username = obj.get("username").getAsString();
			 password = obj.get("password").getAsString();
			 auth_url = obj.get("auth_url").getAsString(); // aggiungi /v3
			 domain = obj.get("domainName").getAsString();
			 project = obj.get("object_storage_9b43b72e_b99c_4478_a712_ee5a0356e6be").getAsString();
			 projectId = obj.get("f4f868ec65bd4f69924e61f03107e63f").getAsString();
			 region = obj.get("region").getAsString();
	            
		} else {
			// If VCAP_SERVICES env var doesn't exist: running locally.
			// Replace these values with your Cloudant credentials

			System.out.println("[createObjectStorage()] VCAP_SERVICES == null");

			userId = Configs.oo_userId;
			username = Configs.oo_username;
			password = Configs.oo_password;
			auth_url = Configs.oo_auth_url; // aggiungi /v3
			domain = Configs.oo_domain;
			project = Configs.oo_project;
			projectId = Configs.oo_projectId;
			region = Configs.oo_region;
		}

		return new ObjectStorage(userId, username, password, domain, projectId, project, region, auth_url);
	}

	private ObjectStorageClientMgr() {
	}
}
