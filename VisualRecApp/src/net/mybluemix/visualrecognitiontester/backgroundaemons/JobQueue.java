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
public class JobQueue {

	LinkedList<Long> queue = new LinkedList<Long>();

	public synchronized void addJob(Long id) {

		System.out.println("JobQueue(): addJob(" + id + ")");
		queue.addLast(id);
		notify();
	}

	public synchronized Long getJob() throws InterruptedException {
		while (queue.isEmpty()) {
			wait();
		}
		return queue.removeFirst();
	}
}
