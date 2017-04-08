package net.mybluemix.visualrecognitiontester.backgroundaemons.datamodel;

import java.util.LinkedList;

/**
 * This class represents a queue of jobs. It is synchronized, so it will
 * be accessed only by one thread at time. It's accessed by servlet (producers),
 * and by demons (producer/consumers)
 * 
 * @author Marco Dondio
 *
 */
public class JobQueue<T> {

	// TODO capire come è meglio fare: linkedmap?
	LinkedList<T> queue = new LinkedList<T>();

	public synchronized void addJob(T job) {

//		System.out.println("[JobQueue]: addJob(" + job + ")");
		queue.addLast(job);
		
		notify();
	}

	public synchronized T getJob() throws InterruptedException {
		while (queue.isEmpty()) {
			
//			System.out.println("[JobQueue] getJob() waiting...");
			wait();

		}
//		System.out.println("[JobQueue] Returning value...");

		return queue.removeFirst();
	}

}
