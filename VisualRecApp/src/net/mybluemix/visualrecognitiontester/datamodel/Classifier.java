package net.mybluemix.visualrecognitiontester.datamodel;

import java.util.Date;

/**
 * Class to represent a Classifier Object
 * @author Marco Dondio
 *
 */
@SuppressWarnings("unused")
public class Classifier {
	private String _id;
	private String type;
	private String instance;	// api_key of instance containig this classifier
	private String label;
	private int training_size;
//	private Date zombie_date; // TODO
	
	// TODO ricordati di gestire date in JSON
//	 GsonBuilder builder = new GsonBuilder();
//	    builder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//
//	    CloudantClient client = ClientBuilder.account("example").username("user").password("password)
//	            .gsonBuilder(builder).build();

	private String status;
	private String training_set;
	
	public String getID(){
		return _id;
	}
	
	public String getApiKey(){
		return instance;
	}
	
	public String getLabel(){
		return label;
	}
	
	public int getTrainingSize(){
		return training_size;
	}
	
	public void setStatus(String status){
		this.status = status;
	}

//	public void setZombieDate(Date zombie_date) {
//this.zombie_date = zombie_date;	
//	}
	
}
