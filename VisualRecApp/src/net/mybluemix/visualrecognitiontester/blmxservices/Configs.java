package net.mybluemix.visualrecognitiontester.blmxservices;

/**
 * This class contains configuration information regarding BlueMix Services.
 * These parameters are valid and will be used only when running in local
 * Liberty server. If application is run inside Bluemix, VCAP_SERVICES variable
 * is used instead
 * 
 * @author Marco Dondio
 *
 */
public class Configs {

	///////////////////////////////////////////////////////////
	// Marco Cloudant DB Instance
//	public static final String CLOUDANT_USER = "12aa609a-c692-42aa-b848-c46ea9dfe117-bluemix";
//	public static final String CLOUDANT_PASS = "b3cb397d32d9e4860f9d14406d221c02c707127e97f858b2191693b75e290350";
//	public static final String DATABASENAME = "visualrec_db_new";

	// Gianvito Cloudant DB Instance
	public static final String CLOUDANT_USER = "2dc3e7f2-72f3-4671-abee-f8fe4463f639-bluemix";
	public static final String CLOUDANT_PASS = "e4e12c476026b63264aed5bfb997a0df8580a4997cb3d7b8af150b9f6b38f630";
	public static final String DATABASENAME = "visualrec";
		
	
	///////////////////////////////////////////////////////////
	// Marco Object Storage UK
	public static final String OO_DEFAULTCONTAINER = "images_london";

	public static final String oo_userId = "c406748da68f49a3b2f57b3395ce6844";
	public static final String oo_username = "admin_57d22f04b7848bcedfe8b13bf4a453d587490247";
	public static final String oo_password = "Wkd#/kd,8&SKOsU3";
	// public static final String oo_auth_url =
	// "https://lon-identity.open.softlayer.com";
	public static final String oo_auth_url = "https://identity.open.softlayer.com";
	public static final String oo_domain = "1187923";
	public static final String oo_project = "object_storage_4df0655d_7096_4a90_b734_00079ecb5f78";
	public static final String oo_projectId = "4ad8f0290e654ae9beadac2d807426be";
	public static final String oo_region = "london";

	

	
	
}
