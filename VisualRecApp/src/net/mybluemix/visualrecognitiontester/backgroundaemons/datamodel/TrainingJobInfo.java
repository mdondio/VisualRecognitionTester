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

	public TrainingJobInfo(Dataset dataset, String label) {
		this.dataset = dataset;
		this.label = label;
	}


	public Dataset getDataset() {
		return dataset;
	}
	public String getLabel() {
		return label;
	}

	public String toString(){
		return "Dataset: " + dataset.getId() + " label: " + label;
		
	}
}
