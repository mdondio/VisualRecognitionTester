package net.mybluemix.visualrecognitiontester.backgroundaemons;

import javax.servlet.ServletContext;

/**
 * This daemon will run in background, blocking and waiting for a job passed by
 * JobQueue object.
 * 
 * @author Marco Dondio
 *
 */
public class AsyncJobDaemon implements Runnable {

	// Setup context info
	private ServletContext ctx;
	private JobQueue queue;
	private int jobCounter = 0;

	public AsyncJobDaemon(ServletContext ctx) {

		System.out.println("[AsyncJobDaemon] Constructor");
		this.ctx = ctx;

	}

	public void initialize(){
		queue = (JobQueue) ctx.getAttribute("jobQueue");
		ctx.setAttribute("jobCounter", jobCounter);
		
	}
	
	public void run() {

		System.out.println("[AsyncJobDaemon] Run in execution (thread: " + Thread.currentThread().getName() + ")...");
		initialize();
		
		while (true) {

			// wait for a new job
			Long id;
			try {
				id = queue.getJob();
			} catch (InterruptedException e) {
				break;
			}

			System.out.println("[AsyncJobDaemon] Job received with id = " + id + ". Processing...");

			// TODO do something
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				break;
			}

			// Job done: increment counter
			System.out.println("[AsyncJobDaemon] Job completed! Incrementing counter to " + ++jobCounter);
			ctx.setAttribute("jobCounter", jobCounter);
		}
		System.out.println("[AsyncJobDaemon] Terminating");

	}

}
