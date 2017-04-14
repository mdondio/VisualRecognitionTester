package net.mybluemix.visualrecognitiontester.backgroundaemons.datamodel;

import java.util.List;

import javax.servlet.http.Part;


/**
 * This small class represents all the info needed to creat a dataset job
 * 
 * @author Marco Dondio
 *
 */
public class DatasetJobInfo {

	public static enum TYPE {
		INSERT, DELETE
	};

	String datasetId;
	String label;
	TYPE type;
	List<Part> positives;
	List<Part> negatives;

	public DatasetJobInfo(String datasetId, TYPE type) {
		this.datasetId = datasetId;
		this.type = type;
	}

	
	public void setPositives(List <Part> positives2){
		this.positives = positives2;
		
	}
	public void setNegatives(List<Part> negatives){
		this.negatives = negatives;
	}
	
	public List<Part> getPositives(){
		return positives;
		
	}
	public List<Part> getNegatives(){
		return negatives;
	}
	
	
	public String getDatasetId() {
		return datasetId;
	}

	public TYPE getType() {
		return type;
	}

	public String toString() {
		return "DatasetInfo datasetId: " + datasetId + " type: " + type;
	}


	public void setLabel(String label) {
this.label = label;		
	}


	public String getLabel() {
		return label;
	}

}
