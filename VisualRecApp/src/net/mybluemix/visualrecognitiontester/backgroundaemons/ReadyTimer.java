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
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassifier;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassifier.Status;

import net.mybluemix.visualrecognitiontester.blmxservices.CloudantClientMgr;
import net.mybluemix.visualrecognitiontester.blmxservices.Configs;
import net.mybluemix.visualrecognitiontester.blmxservices.ObjectStorage;
import net.mybluemix.visualrecognitiontester.blmxservices.ObjectStorageClientMgr;
import net.mybluemix.visualrecognitiontester.blmxservices.marcovisualreclibrary.Utils;
import net.mybluemix.visualrecognitiontester.blmxservices.marcovisualreclibrary.WatsonBinaryClassifier;
import net.mybluemix.visualrecognitiontester.blmxservices.marcovisualreclibrary.exceptions.VisualClassifierException;
import net.mybluemix.visualrecognitiontester.datamodel.Classifier;

/**
 * This daemon runs periodically to test all non ready classifiers.
 * 
 * @author Marco Dondio
 *
 */
public class ReadyTimer extends TimerTask {

	public ReadyTimer(ServletContext ctx) {
	}

	@Override
	public void run() {
		System.out.println("[ReadyTimer] Checking all classifiers: " + new Date());
		try {
			checkClassifiers();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("[ReadyTimer] Sleeping: " + new Date());
	}

	private void checkClassifiers() throws IOException {

		Database db = CloudantClientMgr.getCloudantDB();

		// retrieve all non ready classifiers
		String selector = "{\"selector\": {\"type\":\"classifier\", \"status\":{\"$ne\":\"ready\"}}}";

		// Limita i campi
		FindByIndexOptions o = new FindByIndexOptions().fields("_id").fields("label").fields("status")
				.fields("instance").fields("zombie_since");

		// execute query
		List<Classifier> classifiers = db.findByIndex(selector, Classifier.class, o);

		for (Classifier c : classifiers) {
			System.out.println("[ReadyTimer] Checking zombie classifierId " + c.getID() + "...");

			// TODO
			// TODO
			// TODO
			// TODO
			// TODO
			// TODO controllo per quelli che sono in training.. dovrei leggere i
			// detail...
			// invece per gli zombie va bene attuale controllo

			// if ready, set to ready in cloudant
			// and remove from queueù

			if (c.getStatus() == "zombie" && isZombieClassifierReadyAgain(c)) {
				// if (isClassifierReadyAgain(classifier)) {
				System.out.println("[ReadyTimer] zombie " + c.getID() + " became ready! Setting ready in cloudant!");
				setReady(c.getID());
			} else if (c.getStatus() == "training" && isTrainingClassifierReadyAgain(c)) {
				// if (isClassifierReadyAgain(classifier)) {
				System.out.println("[ReadyTimer] training " + c.getID() + " became ready! Setting ready in cloudant!");
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

	private boolean isZombieClassifierReadyAgain(Classifier c) throws IOException {

		// Get instance
		ObjectStorage oo = ObjectStorageClientMgr.getObjectStorage();

		// build a small zip just to check classifier
		byte[] zip = buildDummyZip(oo, Configs.OO_DEFAULTCONTAINER, "zombie_test.jpg");

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

	private boolean isTrainingClassifierReadyAgain(Classifier c) throws IOException {

		// Instantiate service
		VisualRecognition service = new VisualRecognition(VisualRecognition.VERSION_DATE_2016_05_20);
		service.setApiKey(c.getApiKey());

		VisualClassifier classifier = service.getClassifier(c.getID()).execute();

		return classifier.getStatus() == Status.AVAILABLE;
	}

	private void setReady(String classifierId) {

		// get db connection
		Database db = CloudantClientMgr.getCloudantDB();

		// Get the classifier from db
		Classifier c = db.find(Classifier.class, classifierId);

		// Update classifier
		c.setStatus("ready");
		// c.setZombieSince(null);

		// now update the remote classifier
		Response responseUpdate = db.update(c);

		System.out.println("[ReadyTimer] Updated cloudant db, response: " + responseUpdate);

	}

}
