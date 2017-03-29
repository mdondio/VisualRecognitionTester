package net.mybluemix.visualrecognitiontester.backgroundaemons;

import java.util.LinkedList;

/**
 * This class represents a queue of jobs (long). It is synchronized, so it will
 * be accessed only by one thread at time. It's accessed by servlet (producers),
 * and by demons (producer/consumers)
 * 
 * @author Marco Dondio
 *
 */
public class JobQueue<T> {

	// TODO capire come è meglio fare: linkedmap?
	LinkedList<T> queue = new LinkedList<T>();

	public synchronized void addJob(T id) {

//		System.out.println("[JobQueue]: addJob(" + id + ")");
		queue.addLast(id);
		
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
