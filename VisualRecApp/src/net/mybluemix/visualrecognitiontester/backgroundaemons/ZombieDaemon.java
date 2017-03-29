package net.mybluemix.visualrecognitiontester.backgroundaemons;


import javax.servlet.ServletContext;

import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.Response;

import net.mybluemix.visualrecognitiontester.blmxservices.CloudantClientMgr;
import net.mybluemix.visualrecognitiontester.datamodel.Classifier;

/**
 * This daemon will run in background, blocking and waiting for a job passed by
 * JobQueue object.
 * Each job is a classifier that needs to be set as zombie
 * in cloudant, and passed to monitor for future reactivation
 * 
 * @author Marco Dondio
 *
 */
public class ZombieDaemon implements Runnable {

	// Setup context info
	private ServletContext ctx;
	private JobQueue<Job<String>> zombieQueue;
	private JobQueue<Job<String>> zombieMonitorQueue;
	
	public ZombieDaemon(ServletContext ctx) {

		System.out.println("[ZombieDaemon] Constructor");
		this.ctx = ctx;

	}

	@SuppressWarnings("unchecked")
	public void initialize(){
		zombieQueue = (JobQueue<Job<String>>) ctx.getAttribute("zombieQueue");
		zombieMonitorQueue = (JobQueue<Job<String>>) ctx.getAttribute("zombieMonitorQueue");
	}
	
	public void run() {

		System.out.println("[ZombieDaemon] Run in execution (thread: " + Thread.currentThread().getName() + ")...");
		initialize();
		
		while (true) {

			// wait for a new job
			Job<String> classifier = null;
			try {
				classifier = zombieQueue.getJob();
			} catch (InterruptedException e) {
			}

			System.out.println("[ZombieDaemon] Classifier received = " + classifier + ". Processing...");

			// Set as zombie current classifier
			setAsZombie(classifier);
			
			// Now add this classifier to the zombiemonitor
			// for future re-activation
			System.out.println("[ZombieDaemon] Passing " + classifier + " to ZombieMonitorDaemon...");

			zombieMonitorQueue.addJob(classifier);
			
			 
//			try {
//				Thread.sleep(3000);
//			} catch (InterruptedException e) {
//				break;
//			}
			//----------------------------------------------------------
			System.out.println("[ZombieDaemon] Job completed!");
		}
	//	System.out.println("[ZombieDaemon] Terminating");

	}

	private void setAsZombie(Job<String> classifier) {

		// get db connection
		Database db = CloudantClientMgr.getCloudantDB();
		
		// Get the classifier from db
		 Classifier c = db.find(Classifier.class, classifier.getID());
		 
		// Update classifier
		 c.setStatus("zombie");
//		 c.setZombieDate(classifier.getDate());
		 
		 // now update the remote classifier
		  Response responseUpdate = db.update(c);

		  System.out.println("[ZombieDaemon] Updated cloudant db, response: " + responseUpdate);
		  
	}

}
