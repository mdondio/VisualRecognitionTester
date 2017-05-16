package net.mybluemix.visualrecognitiontester.datamodel;

import java.util.Date;

/**
 * Class to represent a Classifier Object
 * 
 * @author Marco Dondio
 *
 */
@SuppressWarnings("unused")
public class Classifier {
	private String _id;
	private String _rev;
	private String type;
	private String instance; // api_key of instance containig this classifier
	private String label;
	private int training_size;
	private Date zombie_since;
	private String description;
	private String observations;
	private String shortname;

	// TODO ricordati di gestire date in JSON
	// GsonBuilder builder = new GsonBuilder();
	// builder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	//
	// CloudantClient client =
	// ClientBuilder.account("example").username("user").password("password)
	// .gsonBuilder(builder).build();

	private String status;
	private String training_set;

	public void setId(String id){
		this._id = id;
	}
	
	public String getID() {
		return _id;
	}

	public String getApiKey() {
		return instance;
	}

	public String getLabel() {
		return label;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getObservations() {
		return observations;
	}
	
	public String getShortname() {
		return shortname;
	}

	public int getTrainingSize() {
		return training_size;
	}

	public void setStatus(String status){
		this.status = status;
	}

	 public void setZombieSince(Date zombie_since) {
	this.zombie_since = zombie_since;
	}

	public String setDescription(String description) {
		return this.description = description;
	}

	public String setObservations(String observations) {
		return this.observations = observations;
	}

	public String setShortname(String shortname) {
		return this.shortname = shortname;
	}
		
	public String toString(){
		return _id;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setInstance(String instance) {
		this.instance = instance;
	}

	public void setLabel(String label) {
this.label = label;		
	}

	public void setTrainingSize(int size) {
		this.training_size = size;
	}

	public void setTrainingSet(String training_set) {
		this.training_set = training_set;
	}

	public String getStatus() {
		return status;
	}

}
