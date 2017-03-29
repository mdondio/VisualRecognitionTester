package net.mybluemix.visualrecognitiontester.backgroundaemons;

import java.util.Date;

/**
 * This small class represents a Job, in terms of classifierId and date
 * @author Marco Dondio
 *
 */
public class Job<T> {

	T id;
	Date date;
	
	public Job(T id){
		this.id = id;
		date = new Date();
	}
	
public T getID(){
	return id;
}	
public Date getDate(){
	return date;
}	
	public String toString(){
		return id + " - " + date;
	}
}
