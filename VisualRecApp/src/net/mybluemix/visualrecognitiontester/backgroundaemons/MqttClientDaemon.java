package net.mybluemix.visualrecognitiontester.backgroundaemons;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.ServletContext;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * This daemon will run in background, reading periodically data from a MQTT
 * topic and keeping a status.
 * 
 * @author Marco Dondio
 *
 */
// https://eclipse.org/paho/clients/java/

// TODO vedi esempio 1
// occhio, forse vorrei cleansession false
// https://github.com/eclipse/paho.mqtt.java/issues/9
public class MqttClientDaemon implements Runnable, MqttCallbackExtended {

	private ServletContext ctx;

	// mqtt channel info
	private MqttConnectOptions conOpt;
	private MqttClient mqttclient;
	private final String clientID = "donde_server";

	// private final String mqttServerURL = "tcp://broker.hivemq.com:1883";
	// private final String topic = "donde_arduino/digital/D7";
	private final String mqttServerURL = "tcp://m2m.eclipse.org:1883";
	private final String topic = "donde_arduino/digital/D7";

	private DateFormat df;

	// information regarding room:
	// number of scan with empty
	// room to be considered
	private static final int EMPTY_THRESHOLD = 15;
	private int roomEmptyCounter = 0;

	public MqttClientDaemon(ServletContext ctx) throws MqttException {

		System.out.println("[MqttClientDaemon] Constructor");
		this.ctx = ctx;
	}

	// initialize code is executed by run method, this means
	// it will be executed by the thread running this class
	private void initialize() {
		// initialize room info
		ctx.setAttribute("roomLastScan", "no scan yet");
		ctx.setAttribute("roomIsFree", false);

		// setup timezone
		// Use Madrid's time zone to format the date in
		df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		df.setTimeZone(TimeZone.getTimeZone("Europe/Madrid"));
	}

	private void initMqtt() throws MqttException {

		// Construct the connection options object that 
		//contains connection parameters
		// such as cleanSession and LWT
		conOpt = new MqttConnectOptions();
		
		// XXX false forse permette di
		// non doversi risottoscrivere
		conOpt.setCleanSession(true); 
		conOpt.setAutomaticReconnect(true);

		// Construct an MQTT blocking mode client
		MemoryPersistence persistence = new MemoryPersistence();

		mqttclient = new MqttClient(mqttServerURL, clientID, persistence);

		// Set this wrapper as the callback handler
		mqttclient.setCallback(this);

		// Connect to the MQTT server
		mqttclient.connect(conOpt);

		// Subscribe to topic
	//	mqttclient.subscribe(topic);

	}

	public void run() {

		System.out.println("[MqttClientDaemon] Run in execution (thread: " + Thread.currentThread().getName() + ")...");
		// Initialize mqtt client
		initialize();

		try {
			// Initialize mqtt client
			initMqtt();

		} catch (MqttException e) {

			e.printStackTrace();
		}

		while (true) {

			// Do nothing, wait for events
			// Thread.sleep(500);
		}

	}

	@Override
	public void connectionLost(Throwable cause) {

		System.out.println("[MqttClientDaemon] Connection to mqtt broker lost!");

	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {

	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		// Called when a message arrives from the server that matches any
		// subscription made by the client

		// System.out.println("[MqttClientDaemon] messageArrived: ");

		Date d = new Date();
		// System.out.println("Time:\t" + df.format(d) + " Topic:\t" + topic + "
		// Message:\t"
		// + new String(message.getPayload()) + " QoS:\t" + message.getQos());

		handleMessage(d, message);

	}

	// This method stores the state of the room
	// it will update context;
	private void handleMessage(Date d, MqttMessage message) {

		// System.out.println("[MqttClientDaemon] handle Message: ");

		// 1 means something, 0 means nothing (or standing still)
		String msg_payload = new String(message.getPayload());
		Boolean presenceFound = msg_payload.contains("1");

		// System.out.println("[MqttClientDaemon] presenceFound: " +
		// presenceFound);

		// Set last scan date
		ctx.setAttribute("roomLastScan", df.format(d));

		// And set room status
		if (!presenceFound) { // sensor does not find movement
			roomEmptyCounter++;

			System.out.println(
					"[MqttClientDaemon] no presence found in the room, roomEmptyCounter =  " + roomEmptyCounter);

			if (roomEmptyCounter > EMPTY_THRESHOLD) // if no movement for at
													// least 15 times, consider
													// it free
			{
				ctx.setAttribute("roomIsFree", true);
				System.out.println("[MqttClientDaemon] counter above Threshold: roomIsFree set to true");
			}

		} else { // movement found, reset and set room to occupied status
			roomEmptyCounter = 0;
			System.out
					.println("[MqttClientDaemon] Presence found in the room, roomEmptyCounter =  " + roomEmptyCounter);
			ctx.setAttribute("roomIsFree", false);
			System.out.println("[MqttClientDaemon] roomIsFree set to false");

		}

	}

	@Override
	public void connectComplete(boolean reconnect, String uri) {

		if (reconnect) {
			System.out.println("[MqttClientDaemon] Automatically Reconnected to "+uri+"!");

		} else
			System.out.println("[MqttClientDaemon] Connected To "+uri+" for the first time!");

		// Subscribe to topic
		try {
			System.out.println("[MqttClientDaemon] Subscribing to " + topic);

			this.mqttclient.subscribe(topic);
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
