package net.mybluemix.visualrecognitiontester.backgroundaemons;

import java.io.IOException;

import javax.servlet.ServletContext;

import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.Response;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassifier;

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
				wbc = new WatsonBinaryClassifier(vr_instance.getApiKey(), trainingJob.getObj().getLabel(),
						positiveClassZip, negativeClassZip);
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

			// Now update cloudant...
			// XXX ATTENZIONE
			// potenzialmente rischio di non atomicità nell'operazione
			// inconsistenze!
			// attenzione, se ho degli errori qui sotto cosa faccio?
			// converrebbe fare un delete model nel dubbio cosi api key diventa
			// "free"
			updateCloudant(trainingJob.getObj(), wbc);

			// se non ho avuto errori sopra.. tutto ok!

			System.out.println("[TrainDaemon] Job completed!");
		}
	}

	// XXX attenzione: potenzialmente non atomico.. occhio ad inconsistenza
	// possibili
	// meglio avere primo documento classificatore, poi inserirlo in instance
	private void updateCloudant(TrainingInfo info, WatsonBinaryClassifier wbc) {
		classifierInsert(info, wbc);
		instanceUpdate(info, wbc);
	}

	private void instanceUpdate(TrainingInfo info, WatsonBinaryClassifier wbc) {

		// get db connection
		Database db = CloudantClientMgr.getCloudantDB();

		// Get the instance from db
		Instance i = db.find(Instance.class, info.getInstance().getId());

		// add this classifier
		i.addClassifier(wbc.getClassifierId());

		// now update the remote classifier
		Response responseUpdate = db.update(i);

		System.out.println("[TrainDaemon] Updated Instance, response: " + responseUpdate);
	}

	private void classifierInsert(TrainingInfo info, WatsonBinaryClassifier wbc) {

		// TODO build classifier from wbc
		// XXX capire se ho problemi a mettere un costruttore
		Classifier c = new Classifier();
		c.setId(wbc.getClassifierId());
		c.setType("classifier");
		c.setInstance(info.getInstance().getApiKey());
		c.setLabel(wbc.getLabel());
		c.setTrainingSize(info.getDataset().getSize());
		c.setStatus("training");
		c.setTrainingSet(info.getDataset().getId());

		// get db connection
		Database db = CloudantClientMgr.getCloudantDB();

		// Insert obj
		Response responsePost = db.post(c);

		System.out.println("[TrainDaemon] Inserted classifier, response: " + responsePost);
	}

}
