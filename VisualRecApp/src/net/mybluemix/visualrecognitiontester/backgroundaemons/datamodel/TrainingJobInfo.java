package net.mybluemix.visualrecognitiontester.backgroundaemons.datamodel;

import net.mybluemix.visualrecognitiontester.datamodel.Dataset;
import net.mybluemix.visualrecognitiontester.datamodel.Instance;

/**
 * This small class represents all the info needed to creat a training job
 * 
 * @author Marco Dondio
 *
 */
public class TrainingJobInfo {

	Dataset dataset;
	String label;
	String description;
	String shortname;
	String comments;
	
	public TrainingJobInfo(Dataset dataset, String label, String description, String shortname, String comments) {
		this.dataset = dataset;
		this.label = label;
		this.description = description;
		this.shortname = shortname;
		this.comments = comments;
	}


	public Dataset getDataset() {
		return dataset;
	}
	public String getLabel() {
		return label;
	}

	public String getDescription(){
		return description;
	}
	
	public String getShortname(){
		return shortname;
	}
	
	public String getComments(){
		return comments;
	}
	
	public String toString(){
		return "Dataset: " + dataset.getId() + " label: " + label;
		
	}
}
