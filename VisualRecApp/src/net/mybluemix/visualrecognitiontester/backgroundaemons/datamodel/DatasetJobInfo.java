package net.mybluemix.visualrecognitiontester.backgroundaemons.datamodel;

import java.awt.image.BufferedImage;
import java.util.List;



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
	List<BufferedImage> positives;
	List<BufferedImage> negatives;

	public DatasetJobInfo(String datasetId, TYPE type) {
		this.datasetId = datasetId;
		this.type = type;
	}

	
	public void setPositives(List<BufferedImage> positives){
		this.positives = positives;
		
	}
	public void setNegatives(List<BufferedImage> negatives){
		this.negatives = negatives;
	}
	
	public List<BufferedImage> getPositives(){
		return positives;
		
	}
	public List<BufferedImage> getNegatives(){
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
