package net.mybluemix.visualrecognitiontester.backgroundaemons.datamodel;

//import java.awt.image.BufferedImage;
import java.io.File;
//import java.util.List;



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
	String description;
	String comment;
	TYPE type;
	File datasetStagingArea;
	int posCounter;
	int negCounter;
//	List<BufferedImage> positives;
//	List<BufferedImage> negatives;

	public DatasetJobInfo(String datasetId, TYPE type) {
		this.datasetId = datasetId;
		this.type = type;
	}

	
//	public void setPositives(List<BufferedImage> positives){
//		this.positives = positives;
//		
//	}
//	public void setNegatives(List<BufferedImage> negatives){
//		this.negatives = negatives;
//	}
	
//	public List<BufferedImage> getPositives(){
//		return positives;
//		
//	}
//	public List<BufferedImage> getNegatives(){
//		return negatives;
//	}
//	
	
	public String getDatasetId() {
		return datasetId;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description=description;
	}

	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment=comment;
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


	public void setStagingFolder(File datasetStagingArea) {
		this.datasetStagingArea = datasetStagingArea;
	}

	public File getStagingArea() {
		return datasetStagingArea;
	}


	public void setPosCounter(int posCounter) {
		this.posCounter = posCounter;
		
	}
	public void setNegCounter(int negCounter) {
		this.negCounter = negCounter;
	}

	public int getPosCounter(){
		return posCounter;
	}

	public int getNegCounter(){
		return negCounter;
	}
	
}
