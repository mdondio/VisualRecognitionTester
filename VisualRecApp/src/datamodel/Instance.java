package datamodel;

import java.util.List;

@SuppressWarnings("unused")
public class Instance {
	
//	{
//		  "_id": "instance_a40fce6329c185129d0d6ac72f4a4b22d23ffba1",
//		  "_rev": "2-d51d67211abc5eaba515357772d389b1",
//		  "type": "instance",
//		  "type_of_service": "visual recognition",
//		  "classifiers": [
//		    "helicopter_a40fce6329c185129d0d6ac72f4a4b22d23ffba1"
//		  ]
//		}

	private String _id;
	private String type;
	private String type_of_service;
	
	private List<String> classifiers;

}
