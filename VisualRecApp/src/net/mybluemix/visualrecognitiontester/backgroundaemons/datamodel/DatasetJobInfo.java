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
	
	public String getDatasetId() {
		return datasetId;
	}

	public TYPE getType() {
		return type;
	}

	public String toString() {
		return "DatasetInfo datasetId: " + datasetId;
	}

}
