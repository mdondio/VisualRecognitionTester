package net.mybluemix.visualrecognitiontester.backgroundaemons;

import javax.servlet.ServletContext;

/**
 * This daemon waits for job: each job is a classifier that needs to be
 * "reactivated" in the db (set to ready)
 * 
 * 
 * @author Marco Dondio
 * 
 */
public class ZombieMonitorDaemon implements Runnable {

	// Setup context info
	private ServletContext ctx;
	private JobQueue<Job<String>> zombieMonitorQueue;

	public ZombieMonitorDaemon(ServletContext ctx) {

		System.out.println("[ZombieMonitorDaemon] Constructor");
		this.ctx = ctx;

	}

	@SuppressWarnings("unchecked")
	public void initialize() {
		zombieMonitorQueue = (JobQueue<Job<String>>) ctx.getAttribute("zombieMonitorQueue");

	}

	public void run() {

		System.out.println(
				"[ZombieMonitorDaemon] Run in execution (thread: " + Thread.currentThread().getName() + ")...");
		initialize();

		while (true) {

			// wait for a new job
			Job<String> classifier = null;
			try {
				classifier = zombieMonitorQueue.getJob();
			} catch (InterruptedException e) {
			}

			System.out.println("[ZombieMonitorDaemon] Classifier received = " + classifier + ". Processing...");

			// ----------------------------------------------------------
			// TODO do something

			// va a resettare in cloudnt al valore giusto il classificatore

			// TODO ricordati di gestire date in JSON
//			 GsonBuilder builder = new GsonBuilder();
//			    builder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//
//			    CloudantClient client = ClientBuilder.account("example").username("user").password("password)
//			            .gsonBuilder(builder).build();
			

			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				break;
			}
			// ----------------------------------------------------------
			System.out.println("[ZombieMonitorDaemon] Job completed!");
		}
		System.out.println("[ZombieMonitorDaemon] Terminating");

	}

}
