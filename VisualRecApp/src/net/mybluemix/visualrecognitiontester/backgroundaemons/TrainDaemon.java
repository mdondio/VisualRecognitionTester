package net.mybluemix.visualrecognitiontester.backgroundaemons;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.FindByIndexOptions;
import com.cloudant.client.api.model.Response;
import com.google.gson.JsonObject;

import net.mybluemix.visualrecognitiontester.backgroundaemons.datamodel.Job;
import net.mybluemix.visualrecognitiontester.backgroundaemons.datamodel.JobQueue;
import net.mybluemix.visualrecognitiontester.backgroundaemons.datamodel.TrainingInfo;
import net.mybluemix.visualrecognitiontester.blmxservices.CloudantClientMgr;
import net.mybluemix.visualrecognitiontester.blmxservices.Configs;
import net.mybluemix.visualrecognitiontester.blmxservices.ObjectStorage;
import net.mybluemix.visualrecognitiontester.blmxservices.ObjectStorageClientMgr;
import net.mybluemix.visualrecognitiontester.blmxservices.marcovisualreclibrary.Utils;
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
			Dataset d = trainingJob.getObj().getDataset();

			byte[] posExamples = null;
			byte[] negExamples = null;
			try {
				posExamples = Utils.buildCompressedStream(oo, Configs.OO_DEFAULTCONTAINER,
						d.getImages().getPositives());
				negExamples = Utils.buildCompressedStream(oo, Configs.OO_DEFAULTCONTAINER,
						d.getImages().getNegatives());
			} catch (IOException e) {
				// XXX idealmente non è da skippare, ma da riprovare dopo un po'
				System.out.println("[TrainDaemon] Error: failed to build zips. Skip job.");
				continue;
			}

			// 2 - produce classifier name
			// TODO

			// 3 - send request to watson and wait answer
			// TODO

			// 4 - update cloudant JSON
			// TODO

			System.out.println("[TrainDaemon] Job completed!");
		}
	}

}
