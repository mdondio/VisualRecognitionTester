package net.mybluemix.visualrecognitiontester.backgroundaemons;

import java.io.IOException;

import javax.servlet.ServletContext;

import net.mybluemix.visualrecognitiontester.backgroundaemons.datamodel.DatasetJobInfo;
import net.mybluemix.visualrecognitiontester.backgroundaemons.datamodel.DatasetJobInfo.TYPE;
import net.mybluemix.visualrecognitiontester.backgroundaemons.datamodel.Job;
import net.mybluemix.visualrecognitiontester.backgroundaemons.datamodel.JobQueue;
import net.mybluemix.visualrecognitiontester.blmxservices.ObjectStorage;
import net.mybluemix.visualrecognitiontester.blmxservices.ObjectStorageClientMgr;

/**
 * This daemon will run in background, blocking and waiting for a job passed by
 * JobQueue object. A job can be insertion of new dataset or deletion of new
 * dataset
 * 
 * @author Marco Dondio
 *
 */
public class DatasetDaemon implements Runnable {

	// Setup context info
	private ServletContext ctx;
	private JobQueue<Job<DatasetJobInfo>> datasetQueue;

	public DatasetDaemon(ServletContext ctx) {

		System.out.println("[DatasetDaemon] Constructor");
		this.ctx = ctx;

	}

	@SuppressWarnings("unchecked")
	public void initialize() {
		datasetQueue = (JobQueue<Job<DatasetJobInfo>>) ctx.getAttribute("datasetQueue");
	}

	public void run() {

		System.out.println("[DatasetDaemon] Run in execution (thread: " + Thread.currentThread().getName() + ")...");
		initialize();

		while (true) {

			// wait for a new job
			Job<DatasetJobInfo> datasetJob = null;
			try {
				datasetJob = datasetQueue.getJob();
			} catch (InterruptedException e) {
			}

			System.out.println("[DatasetDaemon] Received = " + datasetJob + ". Processing...");

			// 1 - Retrieve image from object storage and build zips
			ObjectStorage oo = null;
			try {
				oo = ObjectStorageClientMgr.getObjectStorage();
			} catch (IOException e) {
				// XXX idealmente non è da skippare, ma da riprovare dopo un po'
				System.out.println("[DatasetDaemon] Error: failed to connect to object storage. Skip job.");
				continue;

			}

			switch (datasetJob.getObj().getType()) {
			case INSERT:
				System.out.println("[DatasetDaemon] Insert Job");
				handleInsert(datasetJob, oo);
				break;
			case DELETE:
				System.out.println("[DatasetDaemon] Delete Job");
				handleDelete(datasetJob, oo);
				break;
			default:
				System.out.println("[DatasetDaemon] No Job recognized");
				break;
			}

			System.out.println("[DatasetDaemon] Job completed!");
		}
	}
	
	private void handleInsert(Job<DatasetJobInfo> insertJob, ObjectStorage oo) {
		// TODO Auto-generated method stub
		
		// processa tutte le immagini, normalizza
		// part -> bufferedimage
		// normalizza
		// scrivi in oo
		
		// nel mentre costruisci json dataset
		// scrivi in cloudant
		
		
	}


	private void handleDelete(Job<DatasetJobInfo> deleteJob, ObjectStorage oo) {
		// TODO Auto-generated method stub

		// deleta in cloudant

		// deleta tutte le immagini (mi basta lista ID)
		// conviene passare un json dataset nella info
		

	}


}
