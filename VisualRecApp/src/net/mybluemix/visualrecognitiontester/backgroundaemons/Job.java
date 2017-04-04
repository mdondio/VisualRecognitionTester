package net.mybluemix.visualrecognitiontester.backgroundaemons;

import java.util.Date;

/**
 * This small class represents a Job, in terms of classifierId and date
 * @author Marco Dondio
 *
 */
public class Job<T> {

	T obj;
	Date date;
	
	public Job(T obj){
		this.obj = obj;
		date = new Date();
	}
	
public T getObj(){
	return obj;
}	
public Date getDate(){
	return date;
}	
	public String toString(){
		return obj + " - " + date;
	}

}
