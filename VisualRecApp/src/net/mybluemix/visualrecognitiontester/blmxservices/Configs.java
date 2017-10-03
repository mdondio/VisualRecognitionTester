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
	public static final String CLOUDANT_USER = "1639b1fe-9ba5-4746-8980-9b010603f4cc-bluemix";
	public static final String CLOUDANT_PASS = "65bc38f64bceafd7e44a6a23b86081be84db5727ead6be0e8cc8f8576d001e46";
	public static final String DATABASENAME = "visual-rec-db";
		
	
	///////////////////////////////////////////////////////////
	// Marco Object Storage UK
	public static final String OO_DEFAULTCONTAINER = "visual-rec-image-container";

	public static final String oo_userId = "d4315177543142a59337797293680ca0";
	public static final String oo_username = "admin_2ede140c37ed32fa1b901464da3b51e3cb1db5e0";
	public static final String oo_password = "tb.R9zeiNV3l^ER&";
	// public static final String oo_auth_url =
	// "https://lon-identity.open.softlayer.com";
	public static final String oo_auth_url = "https://lon-identity.open.softlayer.com";
	public static final String oo_domain = "1461857";
	public static final String oo_project = "object_storage_3a6631db_3327_49d8_a540_ab6ddbcf1ec6";
	public static final String oo_projectId = "05728fc1729d49b7b77ad0628ddecf0e";
	public static final String oo_region = "london";

	

	
	
}
