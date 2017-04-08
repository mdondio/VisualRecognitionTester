package net.mybluemix.visualrecognitiontester.backgroundaemons;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletContext;

import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.FindByIndexOptions;
import com.cloudant.client.api.model.Response;

import net.mybluemix.visualrecognitiontester.blmxservices.CloudantClientMgr;
import net.mybluemix.visualrecognitiontester.blmxservices.ObjectStorage;
import net.mybluemix.visualrecognitiontester.blmxservices.ObjectStorageClientMgr;
import net.mybluemix.visualrecognitiontester.blmxservices.marcovisualreclibrary.Utils;
import net.mybluemix.visualrecognitiontester.blmxservices.marcovisualreclibrary.WatsonBinaryClassifier;
import net.mybluemix.visualrecognitiontester.blmxservices.marcovisualreclibrary.exceptions.VisualClassifierException;
import net.mybluemix.visualrecognitiontester.datamodel.Classifier;

public class ZombieTimer extends TimerTask {


	public ZombieTimer(ServletContext ctx) {
	}

	@Override
	public void run() {
		System.out.println("[ZombieTimer] Checking all classifiers: " + new Date());
		try {
			checkClassifiers();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("[ZombieTimer] Sleeping: " + new Date());
	}

	private void checkClassifiers() throws IOException {


		Database db = CloudantClientMgr.getCloudantDB();

		
		// retrieve all zombie classifiers
		String selector = "{\"selector\": {\"type\":\"classifier\", \"status\":\"zombie\"}}";


        // Limita i campi
        FindByIndexOptions o = new FindByIndexOptions()
        	 .fields("_id").fields("label").fields("instance").fields("zombie_since");
        
        // execute query
        List<Classifier> classifiers = db.findByIndex(selector, Classifier.class, o);
    
        for(Classifier c : classifiers){
			System.out.println("[ZombieTimer] Checking zombie classifierId " + c.getID() + "...");

			// if ready, set to ready in cloudant
			// and remove from queue
			if (isClassifierReadyAgain(c)) {
				//if (isClassifierReadyAgain(classifier)) {
				System.out.println("[ZombieTimer] " + c.getID()
						+ " became ready! Removing from zombieTimerQueue and set ready in cloudant!");
				setReady(c.getID());
			}
		}

	}

	private byte[] buildDummyZip(ObjectStorage oo, String containerName, String imageName) throws IOException {
		byte data[] = new byte[2048];
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(bos);

		HttpURLConnection con;
		try {
			con = oo.doGet("/" + containerName + "", "/" + imageName);
		} catch (IOException e) {

			System.out.println("[ZombieTimer buildDummyZip()]: error while retrieving " + imageName);
			return null;
		}

		// We have a valid image: add as zip entry
		BufferedInputStream entryStream = new BufferedInputStream(con.getInputStream(), 2048);
		ZipEntry entry = new ZipEntry(imageName);
		zos.putNextEntry(entry);
		int count;
		while ((count = entryStream.read(data, 0, 2048)) > 0) {
			zos.write(data, 0, count);
		}
		entryStream.close();
		zos.closeEntry();

		zos.close();
		return bos.toByteArray();
	}

	private boolean isClassifierReadyAgain(Classifier c) throws IOException {

		// Get instance
		ObjectStorage oo = ObjectStorageClientMgr.getObjectStorage();

		// build a small zip just to check classifier
		byte[] zip = buildDummyZip(oo, "images_london", "zombie_test.jpg");

		// Try classification
		WatsonBinaryClassifier classifier = new WatsonBinaryClassifier(c.getApiKey());
		classifier.setClassifierId(c.getID());
		classifier.setLabel(c.getLabel());
		try {
			 classifier.classify(zip, Utils.WATSONMINSCORE);
		} catch (VisualClassifierException e) {

			System.out.println("[ZombieTimer isClassifierReadyAgain()] VisualClassifierException: " + e.getMessage());
			return false;
		}
		return true;
	}


	private void setReady(String classifierId) {

		// get db connection
		Database db = CloudantClientMgr.getCloudantDB();

		// Get the classifier from db
		Classifier c = db.find(Classifier.class, classifierId);

		// Update classifier
		c.setStatus("ready");
//		 c.setZombieSince(null);

		// now update the remote classifier
		Response responseUpdate = db.update(c);

		System.out.println("[ZombieTimer] Updated cloudant db, response: " + responseUpdate);

	}

}
