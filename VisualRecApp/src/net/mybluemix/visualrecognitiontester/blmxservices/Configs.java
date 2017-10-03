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
	public static final String CLOUDANT_USER = "8f1f8a9d-89c7-43bf-97c8-7e3ecc4857d9-bluemix";
	public static final String CLOUDANT_PASS = "f39a7682c71e18e58dd5bb5e529aea9713bee1837ad7656cfe1b5329cab6dd97";
	public static final String DATABASENAME = "visual-rec-db";
		
	///////////////////////////////////////////////////////////
	// Marco Object Storage UK
	public static final String OO_DEFAULTCONTAINER = "visual-rec-image-container";

	public static final String oo_userId = "d0c8f01e73d647eea360a2bce9c77f29";
	public static final String oo_username = "admin_cf525f70687a8a904919e9aed7c25313ee36fba9";
	public static final String oo_password = "A*34a3D.i_{v()5M";
	// public static final String oo_auth_url =
	// "https://lon-identity.open.softlayer.com";
	public static final String oo_auth_url = "https://lon-identity.open.softlayer.com";
	public static final String oo_domain = "1461857";
	public static final String oo_project = "object_storage_9b43b72e_b99c_4478_a712_ee5a0356e6be";
	public static final String oo_projectId = "3f4846f25f1544dc9a0b92206f27df18";
	public static final String oo_region = "london";
    	
	
	
}
