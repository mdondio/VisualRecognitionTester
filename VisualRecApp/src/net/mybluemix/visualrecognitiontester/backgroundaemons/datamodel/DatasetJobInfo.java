package net.mybluemix.visualrecognitiontester.backgroundaemons.datamodel;

/**
 * This small class represents all the info needed to creat a dataset job
 * 
 * @author Marco Dondio
 *
 */
public class DatasetJobInfo {

	public static enum TYPE{INSERT, DELETE};
	
	String datasetId;
	TYPE type;
	
	
	public DatasetJobInfo(String datasetId, TYPE type) {
		this.datasetId = datasetId;
		this.type = type;
	}

	public String getDatasetId() {
		return datasetId;
	}

	public String toString(){
		return "DatasetInfo datasetId: " + datasetId;
	}

	public TYPE getType() {
return type;		
	}
}
