package net.mybluemix.visualrecognitiontester.datamodel;

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
	
}
