package net.mybluemix.visualrecognitiontester.datamodel;

import java.util.List;

/**
 * Class to represent an Instance Object
 * @author Marco Dondio
 *
 */
@SuppressWarnings("unused")
public class Instance {
	
//	{
//		  "_id": "vr_instance_41c64cf664cff4d4f998f5228e59a4743aed81b5",
//		  "_rev": "4-f380cee808afbaae95242c7b92a05d69",
//		  "type": "visual recognition instance",
//		  "account": "account_Alessandro_Pogliaghi",
//		  "region": "Sydney",
//		  "api_key": "41c64cf664cff4d4f998f5228e59a4743aed81b5",
//		  "classifiers": [
//		    "watch_classifier_763371965"
//		  ]
//		}

	private String _id;
	private String _rev;
	private String type;
	private String account;
	private String region;
	private String api_key;

	private List<String> classifiers;
	
	public String getId(){
		return _id;
	}
	public String getApiKey(){
		return api_key;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public void setAccount(String account) {
		this.account = account;
	}
	
	public void setRegion(String region) {
		this.region = region;
	}
	
	public void setApikey(String apiKey) {
		this.api_key = apiKey;
	}
	
	public void setId(String id){
		
		this._id = id;
		
	}
	
	public void setClassifier(List<String> classifiers) {
		this.classifiers = classifiers;
	}
	
	public void addClassifier(String classifierId){
		if(!classifiers.contains(classifierId))
			classifiers.add(classifierId);
	}
	public void removeClassifier(String classifierId) {
		classifiers.remove(classifierId);
	}
	
	
	public List<String> getClassifiers(){
		return classifiers;
	}
}


