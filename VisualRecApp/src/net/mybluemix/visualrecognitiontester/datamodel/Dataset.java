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
	private String _rev;
	private String type;
//	private String sub_type;
	private String label;
	private Images images;
	
	public void setId(String id) {
		this._id = id;
	}

	public String getId(){
		return _id;
	}
	
	public void setImages(Images images) {
		this.images = images;		
	}
	
	public Images getImages(){
		return images;
	}
	
	public void setLabel(String label){
		this.label = label;
	}
	
	public String getLabel(){
		return label;
	}

	public int getSize() {
		return images.getPositives().size() + images.getNegatives().size();
	}

	public int getPositiveSize() {
		return images.getPositives().size();
	}

	public int getNegativeSize() {
		return images.getNegatives().size();
	}


	public void setType(String type){
		this.type = type;
	}
	public String getType() {

	return type;
	}

//	public String getSubType() {
//
//	return sub_type;
//	}


}
