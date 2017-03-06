package net.mybluemix.visualrecognitiontester.datamodel;

import java.util.List;

/**
 * Class to represent a Images Object
 * @author Marco Dondio
 *
 */
// XXX attenzione: se libreria cloundant non li gestisce correttamente mettili a String
public class Images {
	private List<Long> negative;
	private List<Long> positive;
	

	public List<Long> getPositives(){
		return positive;
	}

	public List<Long> getNegatives(){
		return negative;
	}
}