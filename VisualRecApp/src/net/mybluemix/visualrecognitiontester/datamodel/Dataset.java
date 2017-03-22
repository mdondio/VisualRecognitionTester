package net.mybluemix.visualrecognitiontester.datamodel;

import org.json.simple.JSONObject;

/**
 * Class to represent a Dataset Object
 * @author Marco Dondio
 *
 */

@SuppressWarnings("unused")
public class Dataset {
	
	//{
//	  "_id": "helicopter_training01",
//	  "_rev": "1-624f33eae9fa9a368042121d9308e64c",
//	  "type": "dataset",
//	  "sub_type": "training_set",
//	  "label": "helicopter",
//	  "images": {
//	    "negative": [
//	      "10379972771573823310",
//	      "976757625530008326",
//	      "583853835133452524"
//	    ],
//	    "positive": [
//	      "6436660163582720706",
//	      "486099568930654985",
//	      "10818247647460718465"
//	    ]
//	  }
//	}
	private String _id;
	private String type;
	private String sub_type;
	private String label;
	private Images images;
	
	public String getId(){
		return _id;
	}
	
	public Images getImages(){
		return images;
	}
	
	public String getLabel(){
		return label;
	}

	public int getSize() {

		return images.getPositives().size() + images.getNegatives().size();

	}

	public String getType() {

	return type;
	}

	public String getSubType() {

	return sub_type;
	}
}
