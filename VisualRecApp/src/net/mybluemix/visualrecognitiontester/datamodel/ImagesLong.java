package net.mybluemix.visualrecognitiontester.datamodel;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to represent a Images Object
 * 
 * @author Marco Dondio
 *
 */
public class ImagesLong {
	// Purtroppo è molto inefficiente, devo trovare una strada sensata per
	// istruire il gson a parsare i long
	// XXX attenzione: se libreria cloundant non li gestisce correttamente
	// mettili a String
	// [ERROR ] SRVE0777E: Exception thrown by application class
	// 'com.google.gson.internal.bind.TypeAdapters$11.read:323'
	// com.google.gson.JsonSyntaxException: java.lang.NumberFormatException: For
	// input string: "10069764009836769403"

	private List<Long> positive;
	private List<Long> negative;

	public ImagesLong(Images images) {

		positive = new ArrayList<Long>();
		for (String id : images.getPositives())
			positive.add(Long.parseUnsignedLong(id));

		negative = new ArrayList<Long>();
		for (String id : images.getNegatives())
			negative.add(Long.parseUnsignedLong(id));
	}

	public List<Long> getPositives() {
		return positive;
	}

	public List<Long> getNegatives() {
		return negative;
	}

	public static ImagesLong convertFromImages(Images images) {

		if (images == null)
			return null;

		return new ImagesLong(images);
	}
}