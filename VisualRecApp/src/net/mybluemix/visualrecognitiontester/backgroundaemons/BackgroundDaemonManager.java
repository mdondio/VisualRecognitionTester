package net.mybluemix.visualrecognitiontester.backgroundaemons;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.eclipse.paho.client.mqttv3.MqttException;

import net.mybluemix.visualrecognitiontester.backgroundaemons.datamodel.Job;
import net.mybluemix.visualrecognitiontester.backgroundaemons.datamodel.JobQueue;
import net.mybluemix.visualrecognitiontester.backgroundaemons.datamodel.TrainingInfo;
import net.mybluemix.visualrecognitiontester.datamodel.Classifier;


/**
 * This class is responsible to manage all background daemons that will be
 * executed.
 * 
 * @author Marco Dondio
 * 
 */
// http://stackoverflow.com/questions/3084542/background-process-in-servlet
// https://gist.github.com/jamesallman/9331089
// http://www.studytonight.com/servlet/servlet-context-listener.php

@WebListener
public class BackgroundDaemonManager implements ServletContextListener {

	private ServletContext ctx;
	private ExecutorService executor;

	public BackgroundDaemonManager() {
		System.out.println("[BackgroundDaemonManager] Constructor");
	}

	/*
	 * This method is called when the servlet context is initialized(when the
	 * Web application is deployed). You can initialize servlet context related
	 * data here.
	 */
	public void contextInitialized(ServletContextEvent event) {

		System.out.println("[BackgroundDaemonManager] contextInitialized");
		System.out.println("[BackgroundDaemonManager] java vm name: " + System.getProperty("java.vm.name"));
		
		////////////////////////////////////////////
		// Setup context info: add all info
		ctx = event.getServletContext();
		ctx.setAttribute("zombieQueue", new JobQueue<Job<Classifier>>());
		ctx.setAttribute("trainQueue", new JobQueue<Job<TrainingInfo>>());

		////////////////////////////////////////////
		// Prepare all daemons for execution
		List<Runnable> daemons = new ArrayList<Runnable>();
		daemons.add(new ZombieDaemon(ctx));
		daemons.add(new TrainDaemon(ctx));
		
		
		////////////////////////////////////////////
//		System.out.println("[BackgroundDaemonManager] mqtt enabled");
//		try {
//			daemons.add(new MqttClientDaemon(ctx));
//		} catch (MqttException e) {
//			e.printStackTrace();
//		}
		////////////////////////////////////////////

		// Initialize executor: one thread per daemon
		// XXX check se ci sono anche gli scheduled executor
		executor = Executors.newFixedThreadPool(daemons.size());

		// XXX: metodo execute potrebbe usare il current thread?
		for (Runnable daemon : daemons)
			executor.execute(daemon);
		
		// Finally, schedule my timer to check 
		// periodically for not ready classifiers
		TimerTask timerTask = new ReadyTimer(ctx);
        Timer timer = new Timer(true);
        // every 30 mins, check classifiers!
        timer.scheduleAtFixedRate(timerTask, 0, 30*60*1000);
	}

	/*
	 * This method is invoked when the Servlet Context (the Web application) is
	 * undeployed or Application Server shuts down.
	 */
	public void contextDestroyed(ServletContextEvent event) {

		System.out.println("[BackgroundDaemonManager] contextDestroyed");

		// shutdown now sarebbe più brutale
		executor.shutdown();

	}

}