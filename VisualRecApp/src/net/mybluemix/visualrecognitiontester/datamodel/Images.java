package net.mybluemix.visualrecognitiontester.datamodel;

import java.util.List;

/**
 * Class to represent a Images Object
 * @author Marco Dondio
 *
 */
public class Images {
	// Purtroppo è molto inefficiente, devo trovare una strada sensata per istruire il gson a parsare i long
	// XXX attenzione: se libreria cloundant non li gestisce correttamente mettili a String
	//[ERROR   ] SRVE0777E: Exception thrown by application class 'com.google.gson.internal.bind.TypeAdapters$11.read:323'
	//com.google.gson.JsonSyntaxException: java.lang.NumberFormatException: For input string: "10069764009836769403"

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