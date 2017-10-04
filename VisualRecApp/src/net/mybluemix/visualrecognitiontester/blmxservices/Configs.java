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

	///////////////////////////// CLOUDANT DATABASE    //////////////////////////////

	// DB name fixed
	public static final String DATABASENAME = "visual-rec-db";
	
	// Andrea Cloudant DB Instance
	public static final String CLOUDANT_USER = "8f1f8a9d-89c7-43bf-97c8-7e3ecc4857d9-bluemix";
	public static final String CLOUDANT_PASS = "f39a7682c71e18e58dd5bb5e529aea9713bee1837ad7656cfe1b5329cab6dd97";
	
//	// Alessandro Cloudant DB Instance
//	public static final String CLOUDANT_USER = "3e592a1a-0a76-48ce-b689-df1b8fe5ebc7-bluemix";
//	public static final String CLOUDANT_PASS = "bee85ed9536f6f34b2798a71943f2ee6cd6118678c62af52253874167bbe368d";
		
	/////////////////////////////////////   OBJECT STORAGE      //////////////////////
	
	// Container name fixed
	public static final String OO_DEFAULTCONTAINER = "visual-rec-image-container";
	
	// Andrea Object Storage
	public static final String oo_userId = "d0c8f01e73d647eea360a2bce9c77f29";
	public static final String oo_username = "admin_cf525f70687a8a904919e9aed7c25313ee36fba9";
	public static final String oo_password = "A*34a3D.i_{v()5M";
	public static final String oo_auth_url = "https://lon-identity.open.softlayer.com";
	public static final String oo_domain = "1461857";
	public static final String oo_project = "object_storage_9b43b72e_b99c_4478_a712_ee5a0356e6be";
	public static final String oo_projectId = "3f4846f25f1544dc9a0b92206f27df18";
	public static final String oo_region = "london";
    	
//	// Alessandro Object Storage
//	public static final String oo_userId = "66c0963e8e374f3f879f1edbd0626949";
//	public static final String oo_username = "admin_ccb8bb49538ea89069098a6e64ed09e1e6dc5b7b";
//	public static final String oo_password = "Hei#L22HXcoI9ur{";
//	public static final String oo_auth_url = "https://identity.open.softlayer.com";
//	public static final String oo_domain = "1461857";
//	public static final String oo_project = "object_storage_1a2902c7_fa61_472d_91f2_65ae3ff255eb";
//	public static final String oo_projectId = "9474059bf5214ac0a11cd6ccabeac890";
//	public static final String oo_region = "dallas";
	
	
}
