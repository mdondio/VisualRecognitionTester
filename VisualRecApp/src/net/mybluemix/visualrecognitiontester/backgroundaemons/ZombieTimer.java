package net.mybluemix.visualrecognitiontester.backgroundaemons;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Date;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletContext;

import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.Response;

import net.mybluemix.visualrecognitiontester.blmxservices.CloudantClientMgr;
import net.mybluemix.visualrecognitiontester.blmxservices.ObjectStorage;
import net.mybluemix.visualrecognitiontester.blmxservices.ObjectStorageClientMgr;
import net.mybluemix.visualrecognitiontester.blmxservices.marcovisualreclibrary.Utils;
import net.mybluemix.visualrecognitiontester.blmxservices.marcovisualreclibrary.WatsonBinaryClassifier;
import net.mybluemix.visualrecognitiontester.blmxservices.marcovisualreclibrary.exceptions.VisualClassifierException;
import net.mybluemix.visualrecognitiontester.datamodel.Classifier;

public class ZombieTimer extends TimerTask {

	private ConcurrentLinkedQueue<Job<Classifier>> zombieTimerQueue;

	@SuppressWarnings("unchecked")
	public ZombieTimer(ServletContext ctx) {
		this.zombieTimerQueue = (ConcurrentLinkedQueue<Job<Classifier>>) ctx.getAttribute("zombieTimerQueue");
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

		System.out.println("[ZombieTimer] zombieTimerQueue size: " + zombieTimerQueue.size());

		for (Job<Classifier> classifier : zombieTimerQueue) {

			System.out.println("[ZombieTimer] Checking classifierId " + classifier.getObj() + "...");

			// if ready, set to ready in cloudant
			// and remove from queue
			if (true) {
				//if (isClassifierReadyAgain(classifier)) {
				System.out.println("[ZombieTimer] " + classifier.getObj()
						+ " became ready! Removing from zombieTimerQueue and set ready in cloudant!");
				zombieTimerQueue.remove(classifier);
			//	setReady(classifier);
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

	private boolean isClassifierReadyAgain(Job<Classifier> classifierJob) throws IOException {

		Classifier c = classifierJob.getObj();

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


	private void setReady(Job<Classifier> classifier) {

		// get db connection
		Database db = CloudantClientMgr.getCloudantDB();

		// Get the classifier from db
		// XXX forse non necessario
		Classifier c = db.find(Classifier.class, classifier.getObj().getID());

		// Update classifier
		c.setStatus("ready");
		// c.setZombieDate(classifier.getDate());

		// now update the remote classifier
		Response responseUpdate = db.update(c);

		System.out.println("[ZombieTimer] Updated cloudant db, response: " + responseUpdate);

	}

}
