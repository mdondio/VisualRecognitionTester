package net.mybluemix.visualrecognitiontester.backgroundaemons;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.eclipse.paho.client.mqttv3.MqttException;

import net.mybluemix.visualrecognitiontester.backgroundaemons.datamodel.DatasetJobInfo;
import net.mybluemix.visualrecognitiontester.backgroundaemons.datamodel.Job;
import net.mybluemix.visualrecognitiontester.backgroundaemons.datamodel.JobQueue;
import net.mybluemix.visualrecognitiontester.backgroundaemons.datamodel.TrainingJobInfo;

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

	public static final String STAGING_AREA = "staging";

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

		
		// First clean all files from staging area
		cleanDisk();

		////////////////////////////////////////////
		// Setup context info: add all info
		ctx = event.getServletContext();
		ctx.setAttribute("trainQueue", new JobQueue<Job<TrainingJobInfo>>());
		ctx.setAttribute("datasetQueue", new JobQueue<Job<DatasetJobInfo>>());

		////////////////////////////////////////////
		// Prepare all daemons for execution
		List<Runnable> daemons = new ArrayList<Runnable>();
		daemons.add(new TrainDaemon(ctx));
		daemons.add(new DatasetDaemon(ctx));

		////////////////////////////////////////////
		// System.out.println("[BackgroundDaemonManager] mqtt enabled");
		// try {
		// daemons.add(new MqttClientDaemon(ctx));
		// } catch (MqttException e) {
		// e.printStackTrace();
		// }
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
		// every 5 mins, check classifiers!
		timer.scheduleAtFixedRate(timerTask, 0, 5 * 60 * 1000);
	}

	private void cleanDisk() {
		
		// First, clean staging area at startup
		File stagingDir = new File(STAGING_AREA);

		// if it exists, delete everything
		if (stagingDir.exists()) {

			System.out.println("[BackgroundDaemonManager] Staging area exists: cleaning");

			// if existing, each file is a directory
			for (File datasetDir : stagingDir.listFiles()) {
				System.out.println("[BackgroundDaemonManager] Cleaning " + datasetDir);


				for (File images : datasetDir.listFiles())
					images.delete();
				datasetDir.delete();
			}
		} else{
			System.out.println("[BackgroundDaemonManager] Staging area exists: cleaning");
			stagingDir.mkdir();
		}
		
		// Delete other files
		File f = new File("temp_pos.zip");
		f.delete();
		f = new File("temp_neg.zip");
		f.delete();
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