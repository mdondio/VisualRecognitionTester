package net.mybluemix.visualrecognitiontester.backgroundaemons;

import javax.servlet.ServletContext;

/**
 * This daemon will run in background, reading periodically data from a MQTT
 * topic and keeping a status.
 * 
 * @author Marco Dondio
 *
 */
public class MqttClientDaemon implements Runnable {

	// Setup context info
	//private ServletContext ctx;

	public MqttClientDaemon(ServletContext ctx) {

		System.out.println("[MqttClientDaemon] Constructor");
	//	this.ctx = ctx;
	}

	public void run() {

		System.out.println("[MqttClientDaemon] Run in execution (thread: " + Thread.currentThread().getName() + ")...");

		while (true) {

			// TODO to be implemented.. retrieve from mqtt broker
//			System.out.println("[MqttClientDaemon] Retrieving data from mqtt topic...");
//			ctx.setAttribute("mqtt", 1);

			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				break;
			}
		}

		System.out.println("[MqttClientDaemon] Terminating");

	}

}
