package net.mybluemix.visualrecognitiontester.datamodel;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

/**
 * Class needed to implement Dataset with Long values. Needed to handle uncorrect parsing.
 * @author Marco Dondio
 *
 */

@SuppressWarnings("unused")
public class DatasetLong {

	private String _id;
	private String type;
	private String sub_type;
	private String label;

	private ImagesLong images;

	
	public DatasetLong(Dataset d){

		_id = d.getId();
		label = d.getLabel();
		type = d.getType();
		sub_type = d.getSubType();
		
		images = ImagesLong.convertFromImages(d.getImages());
	}
	
	public String getId(){
		return _id;
	}
	
	public ImagesLong getImages(){
		return images;
	}
	
	public String getLabel(){
		return label;
	}

	public int getSize() {
		return images.getPositives().size() + images.getNegatives().size();
	}


	
	public static List<DatasetLong> convertFromDatasets(List<Dataset> datasets) {
		
		if(datasets == null)
			return null;
	
		List<DatasetLong> datasetsLong = new ArrayList<DatasetLong>();

		// Create DatasetLong
		for(Dataset d : datasets ){
			DatasetLong dLong = convertFromDataset(d);
			datasetsLong.add(dLong);
		}

		return datasetsLong;
	}

	public static DatasetLong convertFromDataset(Dataset d) {
		if(d == null)
			return null;

		return new DatasetLong(d);
	}

	public static List<Dataset> convertGToDatasets(List<Dataset> datasets) {
		
		if(datasets == null)
			return null;
		// TODO Auto-generated method stub

		return null;
	}
}
