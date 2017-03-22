package net.mybluemix.visualrecognitiontester.datamodel;

import java.util.List;

/**
 * Class to represent a Images Object
 * @author Marco Dondio
 *
 */
public class Images {

	private List<Long> positives;
	private List<Long> negatives;

	public List<Long> getPositives(){
		return positives;
	}

	public List<Long> getNegatives(){
		return negatives;
	}
	
	public void setPositives(List<Long> positives){
		this.positives = positives;
	}
	public void setNegatives(List<Long> negatives){
		this.negatives = negatives;
		
	}
	
}