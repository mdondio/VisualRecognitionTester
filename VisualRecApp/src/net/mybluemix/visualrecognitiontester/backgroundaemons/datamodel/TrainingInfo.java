package net.mybluemix.visualrecognitiontester.backgroundaemons.datamodel;

import net.mybluemix.visualrecognitiontester.datamodel.Dataset;
import net.mybluemix.visualrecognitiontester.datamodel.Instance;

/**
 * This small class represents all the info needed to creat a training job
 * 
 * @author Marco Dondio
 *
 */
public class TrainingInfo {

	Instance vr_instance;
	Dataset dataset;
	String label;

	public TrainingInfo(Instance vr_instance, Dataset dataset, String label) {
		this.vr_instance = vr_instance;
		this.dataset = dataset;
		this.label = label;
	}

	public Instance getInstance() {
		return vr_instance;
	}

	public Dataset getDataset() {
		return dataset;
	}
	public String getLabel() {
		return label;
	}

	public String toString(){
		return "Instance apiKey: " + vr_instance.getApiKey() + " - dataset: " + dataset.getId() + " label: " + label;
		
	}
}
