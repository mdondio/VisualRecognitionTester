package services;

import java.io.IOException;

import config.Configs;

public class ObjectStorageClient {

	private static ObjectStorage oo = null;

	public static ObjectStorage getObjectStorage() throws IOException {


		if (oo == null) {
			System.out.println("[getObjectStorage()] ObjectStorage is null: need to initialize");
			initClient();
		}
		else{
			System.out.println("[getObjectStorage()] ObjectStorage already instantiated: retrieving");
			
		}

		return oo;

	}

	// Metodo per creare objectstorage
	private static void initClient() throws IOException {

		synchronized (ObjectStorageClient.class) {
			if (oo != null)
				return;

			oo = createObjectStorage();

		} // end synchronized
	}

	private static ObjectStorage createObjectStorage() throws IOException {

		//		{
//			  "auth_url": "https://lon-identity.open.softlayer.com",
//			  "project": "object_storage_e0ebd231_88cf_4cfd_b548_ad95cb9a89e3",
//			  "projectId": "b8240e4d9c1449a6ac7ae6159b116282",
//			  "region": "london",
//			  "userId": "08dab7133ac24cbcaf94520a7954a2f7",
//			  "username": "admin_f1e5d9bfec2240f9889a3d86a157151cb57df47c",
//			  "password": "PQz3*8F7VL3D{owB",
//			  "domainId": "d7b14e49480942beaade5d2cce531404",
//			  "domainName": "1187923",
//			  "role": "admin"
//			}
		
		String userId = null;
		String username = null;
		String password = null;
		String auth_url = null;// qui occhio aggiungi /v3
		String domain = null;
		String project = null;
		String projectId = null;
		String region = null;

		String VCAP_SERVICES = System.getenv("VCAP_SERVICES");
		if (VCAP_SERVICES != null) {

			System.out.println("[createObjectStorage()] VCAP_SERVICES != null");
			// When running in Bluemix, the VCAP_SERVICES env var will have the
			// credentials for all bound/connected services
			// Parse the VCAP JSON structure looking for cloudant.
			// JsonObject obj = (JsonObject) new
			// JsonParser().parse(VCAP_SERVICES);
			// Entry<String, JsonElement> dbEntry = null;
			// Set<Entry<String, JsonElement>> entries = obj.entrySet();
			// // Look for the VCAP key that holds the cloudant no sql db
			// information
			// for (Entry<String, JsonElement> eachEntry : entries) {
			// if (eachEntry.getKey().toLowerCase().contains("cloudant")) {
			// dbEntry = eachEntry;
			// break;
			// }

			// ---- altro esempio json
			// obj = (JsonObject) ((JsonArray) dbEntry.getValue()).get(0);
			// serviceName = (String) dbEntry.getKey();
			// System.out.println("Service Name - " + serviceName);
			//
			// obj = (JsonObject) obj.get("credentials");
			//
			// user = obj.get("username").getAsString();
			// password = obj.get("password").getAsString();
			
			// TODO DA FARE
			System.out.println("[createObjectStorage()] !!!!! TODO, using local info !!!!");

			userId = Configs.oo_userId;
			username = Configs.oo_username;
			password = Configs.oo_password;
			auth_url = Configs.oo_auth_url; // aggiungi /v3
			domain = Configs.oo_domain;
			project = Configs.oo_project;
			projectId = Configs.oo_projectId;
			region = Configs.oo_region;


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

		// TODO try-catch evntuale, prova ad autenticarti
		return new ObjectStorage(userId, username, password, domain, projectId, project, region, auth_url);
	}
	
	private ObjectStorageClient(){
	}
}
