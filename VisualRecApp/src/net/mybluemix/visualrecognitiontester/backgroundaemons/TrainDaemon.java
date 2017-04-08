package net.mybluemix.visualrecognitiontester.backgroundaemons;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.FindByIndexOptions;
import com.cloudant.client.api.model.Response;
import com.google.gson.JsonObject;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassifier;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassifier.Status;

import net.mybluemix.visualrecognitiontester.backgroundaemons.datamodel.Job;
import net.mybluemix.visualrecognitiontester.backgroundaemons.datamodel.JobQueue;
import net.mybluemix.visualrecognitiontester.backgroundaemons.datamodel.TrainingInfo;
import net.mybluemix.visualrecognitiontester.blmxservices.CloudantClientMgr;
import net.mybluemix.visualrecognitiontester.blmxservices.Configs;
import net.mybluemix.visualrecognitiontester.blmxservices.ObjectStorage;
import net.mybluemix.visualrecognitiontester.blmxservices.ObjectStorageClientMgr;
import net.mybluemix.visualrecognitiontester.blmxservices.marcovisualreclibrary.Utils;
import net.mybluemix.visualrecognitiontester.blmxservices.marcovisualreclibrary.WatsonBinaryClassifier;
import net.mybluemix.visualrecognitiontester.datamodel.Classifier;
import net.mybluemix.visualrecognitiontester.datamodel.Dataset;
import net.mybluemix.visualrecognitiontester.datamodel.Instance;

/**
 * This daemon will run in background, blocking and waiting for a job passed by
 * JobQueue object. Each job is a train job to create a new classifier
 * 
 * @author Marco Dondio
 *
 */
public class TrainDaemon implements Runnable {

	// Setup context info
	private ServletContext ctx;
	private JobQueue<Job<TrainingInfo>> trainQueue;

	public TrainDaemon(ServletContext ctx) {

		System.out.println("[TrainDaemon] Constructor");
		this.ctx = ctx;

	}

	@SuppressWarnings("unchecked")
	public void initialize() {
		trainQueue = (JobQueue<Job<TrainingInfo>>) ctx.getAttribute("trainQueue");
	}

	public void run() {

		System.out.println("[TrainDaemon] Run in execution (thread: " + Thread.currentThread().getName() + ")...");
		initialize();

		while (true) {

			// wait for a new job
			Job<TrainingInfo> trainingJob = null;
			try {
				trainingJob = trainQueue.getJob();
			} catch (InterruptedException e) {
			}

			Instance vr_instance = trainingJob.getObj().getInstance();
			Dataset d = trainingJob.getObj().getDataset();
			
			
			System.out.println("[TrainDaemon] Classifier received = " + trainingJob + ". Processing...");

			// 1 - Retrieve image from object storage and build zips
			ObjectStorage oo = null;
			try {
				oo = ObjectStorageClientMgr.getObjectStorage();
			} catch (IOException e) {
				// XXX idealmente non è da skippare, ma da riprovare dopo un po'
				System.out.println("[TrainDaemon] Error: failed to connect to object storage. Skip job.");
				continue;

			}

			// build zips

			byte[] positiveClassZip = null;
			byte[] negativeClassZip = null;
			try {
				positiveClassZip = Utils.buildCompressedStream(oo, Configs.OO_DEFAULTCONTAINER,
						d.getImages().getPositives());
				negativeClassZip = Utils.buildCompressedStream(oo, Configs.OO_DEFAULTCONTAINER,
						d.getImages().getNegatives());
			} catch (IOException e) {
				// XXX idealmente non è da skippare, ma da riprovare dopo un po'
				System.out.println("[TrainDaemon] Error: failed to build zips. Skip job.");
				continue;
			}

			// Train my classifier on watson...
			// XXX attenzione che deve scrivere un file temporaneo... ho paura
			// non funzioni
			WatsonBinaryClassifier wbc = null;
			try {
				wbc = new WatsonBinaryClassifier(vr_instance.getApiKey(),
						trainingJob.getObj().getLabel(), positiveClassZip, negativeClassZip);
			} catch (IOException e) {
				// XXX idealmente non è da skippare, ma da riprovare dopo un po'
				e.printStackTrace();
				System.out.println("[TrainDaemon] Error while training on Watson. Skip job.");
				continue;
			}

			// Now check result: must be training
			if (wbc.getLastStatus() != VisualClassifier.Status.TRAINING) {
				System.out.println("[TrainDaemon] Error while training. Status of classifier: " + wbc.getLastStatus());
				continue;
			}

			// If im here, training was succesfull!
			// TODO need to update cloudant!

			// TODO attenzione, se ho degli errori qui sotto cosa faccio?
			// converrebbe fare un delete model nel dubbio cosi api key diventa
			// "free"
			
			// XXX potenzialmente rischio di non atomicità nell'operazione
			// inconsistenze!
			updateCloudant(vr_instance, wbc);

			// se non ho avuto errori sopra.. tutto ok!
			
			System.out.println("[TrainDaemon] Job completed!");
		}
	}

	// XXX attenzione: potenzialmente non atomico.. occhio ad inconsistenza possibili
	// meglio avere primo documento classificatore, poi inserirlo in instance
	private void updateCloudant(Instance vr_instance, WatsonBinaryClassifier wbc) {
		classifierInsert(vr_instance, wbc);
		instanceUpdate(vr_instance, wbc);
	}

	private void instanceUpdate(Instance vr_instance, WatsonBinaryClassifier wbc) {
		// TODO Auto-generated method stub

	}

	private void classifierInsert(Instance vr_instance, WatsonBinaryClassifier wbc) {
		// TODO Auto-generated method stub

	}

}
