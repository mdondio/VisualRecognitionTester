package net.mybluemix.visualrecognitiontester.backgroundaemons.datamodel;

import java.util.Date;

/**
 * This small class represents a Job, in terms of obj and date of creation
 * 
 * @author Marco Dondio
 *
 */
public class Job<T> {

	T obj;
	Date creationDate;

	public Job(T obj) {
		this.obj = obj;
		creationDate = new Date();
	}

	public T getObj() {
		return obj;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public String toString() {
		return obj + " - " + creationDate;
	}

}
