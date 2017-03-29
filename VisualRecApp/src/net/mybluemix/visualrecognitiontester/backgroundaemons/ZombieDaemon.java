package net.mybluemix.visualrecognitiontester.backgroundaemons;


import java.util.concurrent.ConcurrentLinkedQueue;

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
	private JobQueue<Job<Classifier>> zombieQueue;
	private ConcurrentLinkedQueue<Job<Classifier>> zombieTimerQueue;
	
	public ZombieDaemon(ServletContext ctx) {

		System.out.println("[ZombieDaemon] Constructor");
		this.ctx = ctx;

	}

	@SuppressWarnings("unchecked")
	public void initialize(){
		zombieQueue = (JobQueue<Job<Classifier>>) ctx.getAttribute("zombieQueue");
		zombieTimerQueue = (ConcurrentLinkedQueue<Job<Classifier>>) ctx.getAttribute("zombieTimerQueue");
	}
	
	public void run() {

		System.out.println("[ZombieDaemon] Run in execution (thread: " + Thread.currentThread().getName() + ")...");
		initialize();
		
		while (true) {

			// wait for a new job
			Job<Classifier> classifier = null;
			try {
				classifier = zombieQueue.getJob();
			} catch (InterruptedException e) {
			}

			System.out.println("[ZombieDaemon] Classifier received = " + classifier + ". Processing...");

			// Set as zombie current classifier
			//setAsZombie(classifier);
			
			// Now add this classifier to the zombiemonitor
			// for future re-activation
			System.out.println("[ZombieDaemon] Passing " + classifier + " to zombieTimerQueue...");

			zombieTimerQueue.add(classifier);
				System.out.println("[ZombieDaemon] Job completed!");
		}
	}

	private void setAsZombie(Job<Classifier> classifier) {

		// get db connection
		Database db = CloudantClientMgr.getCloudantDB();
		
		// XXX forse non serve, per sicurezza
		// Get the classifier from db
		 Classifier c = db.find(Classifier.class, classifier.getObj().getID());
		 
		// Update classifier
		 c.setStatus("zombie");
//		 c.setZombieDate(classifier.getDate());
		 
		 // now update the remote classifier
		  Response responseUpdate = db.update(c);

		  System.out.println("[ZombieDaemon] Updated cloudant db, response: " + responseUpdate);
		  
	}

}
