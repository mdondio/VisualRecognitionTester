package datamodel;

import org.json.simple.JSONObject;

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

// http://www.programcreek.com/java-api-examples/index.php?api=com.cloudant.client.api.Database
// https://github.com/ganeshkumarc/java-cloudant-1

// soluzione
//http://stackoverflow.com/questions/2864370/how-do-i-use-googles-gson-api-to-deserialize-json-properly
@SuppressWarnings("unused")
public class Dataset {

	private String _id;
	private String sub_type;
	private String label;
	private String extra;

	private Images images;
}
