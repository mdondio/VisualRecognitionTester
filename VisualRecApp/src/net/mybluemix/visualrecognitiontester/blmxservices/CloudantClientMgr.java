package net.mybluemix.visualrecognitiontester.blmxservices;

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
 * This class is needed to manage the Cloudant DB service instance. This class
 * guarantees we will instantiate only one instance of the service.
 * 
 * @author Marco Dondio
 *
 */
public class CloudantClientMgr {

	private static CloudantClient cloudant = null;
	private static GsonBuilder customGsonBuilder;

	private static Database db = null;

	private static String user = null;
	private static String password = null;

	// This method guarantees we will have only one instance of this object
	public static Database getCloudantDB() {
		if (cloudant == null) {
			initClient();
		}

		if (db == null) {
			try {
				db = cloudant.database(Configs.DATABASENAME, true);
			} catch (Exception e) {
				throw new RuntimeException("DB Not found", e);
			}
		}
		return db;
	}

	public static GsonBuilder getGsonBuilder() {
		return customGsonBuilder;

	}

	private static void initClient() {
		if (cloudant == null) {
			synchronized (CloudantClientMgr.class) {
				if (cloudant != null) {
					return;
				}
				cloudant = createClient();

			} // end synchronized
		}
	}

	private static CloudantClient createClient() {
		
		String VCAP_SERVICES = System.getenv("VCAP_SERVICES");
		String serviceName = null;

		// TODO: stiamo forzando a skippare la VCAP locale
		// questo perchè voglio al momento usare il db alternativo a quello del deploy
		
		// VCAP_SERVICES =null;	
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
				if (eachEntry.getKey().toLowerCase().contains("cloudant")) {
					dbEntry = eachEntry;
					break;
				}
			}
			if (dbEntry == null) {
				throw new RuntimeException("Could not find cloudantNoSQLDB key in VCAP_SERVICES env variable");
			}

			obj = (JsonObject) ((JsonArray) dbEntry.getValue()).get(0);
			serviceName = (String) dbEntry.getKey();
			System.out.println("Service Name - " + serviceName);

			obj = (JsonObject) obj.get("credentials");

			user = obj.get("username").getAsString();
			password = obj.get("password").getAsString();

		} else {
			// If VCAP_SERVICES env var doesn't exist: running locally.
			// Replace these values with your Cloudant credentials

			System.out.println("[createCLient] VCAP_SERVICES NOT FOUND");
			user = Configs.CLOUDANT_USER;
			password = Configs.CLOUDANT_PASS;
		}

		try {
			System.out.println("Connecting to Cloudant : " + user);

			// TODO: necessario per gestire Images internamente come Long
			customGsonBuilder = new GsonBuilder().registerTypeAdapter(Images.class, new ImagesAdapter())
					.registerTypeAdapter(Date.class, new DateAdapter());

			CloudantClient client = ClientBuilder.account(user).username(user).password(password)
					.gsonBuilder(customGsonBuilder).build();
			return client;
		} catch (CouchDbException e) {
			throw new RuntimeException("Unable to connect to repository", e);
		}
	}

	private CloudantClientMgr() {
	}
}
