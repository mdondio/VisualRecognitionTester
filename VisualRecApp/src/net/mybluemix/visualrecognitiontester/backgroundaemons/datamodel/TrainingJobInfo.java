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
	
	public TrainingJobInfo(Dataset dataset, String label, String description, String shortname) {
		this.dataset = dataset;
		this.label = label;
		this.description = description;
		this.shortname = shortname;
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
	
	public String toString(){
		return "Dataset: " + dataset.getId() + " label: " + label;
		
	}
}
