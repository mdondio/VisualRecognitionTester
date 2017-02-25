package myservlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import datamodel.Dataset;

//import org.json.simple.JSONObject;


/**
 * Servlet implementation class TelegramBot
 */
@WebServlet("/AAHV8svrX81twiW48d2N6bpmQuv6BnnKKQU")
public class TelegramBot extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	
	// Hardcoded: Marco BOT
	private static final String apikey = "bot229904529:AAHV8svrX81twiW48d2N6bpmQuv6BnnKKQU";
	

	
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TelegramBot() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		doPost(request, response);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		

//		https://api.telegram.org/bot229904529:AAHV8svrX81twiW48d2N6bpmQuv6BnnKKQU/getUpdates
		System.out.println("[TelegramBot] Received request...");
		
		
		ServletInputStream input = request.getInputStream();
		

	    BufferedReader streamReader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
	    StringBuilder responseStrBuilder = new StringBuilder();

	    String inputStr;
	    while ((inputStr = streamReader.readLine()) != null)
	        responseStrBuilder.append(inputStr);

	    
	    JsonObject update = new JsonParser().parse(responseStrBuilder.toString()).getAsJsonObject(); 

	    System.out.println(update.toString());


	    // TODO send answer!

	    String out = handleUpdate(update);
	    System.out.println("Response: " + out);
		response.getWriter().append(out);
	}
	
	
	private String handleUpdate(JsonObject update) throws IOException{
		

//		{"update_id":921655417,"message":{"message_id":224,"from":{"id":45847711,"first_name":"Marco","last_name":"Dondio"},"chat":{"id":45847711,"first_name":"Marco","last_name":"Dondio","type":"private"},"date":1485817301,"text":"Boh"}}

		JsonObject message = update.getAsJsonObject("message");
		JsonObject chat = message.getAsJsonObject("chat");
		
		String chat_id = chat.get("id").getAsString();
		
		JsonElement first_name = chat.get("first_name");
		JsonElement last_name = chat.get("last_name");
		
		String text = "Ciao " + (first_name == null? "" : first_name.getAsString()) + " " + (last_name == null? "" : last_name.getAsString())  + "! :)";
		

		return sendMessage(chat_id, text);
		
		//		//----------------------------------------------------------
//		// Se non mi va bene il default e lo devo modificare:
//        JsonArray arr = gson.toJsonTree(db.findByIndex(selector, Dataset.class, o)).getAsJsonArray();
////
//        for(int i = 0; i < arr.size(); i++)
//        {
//        	JsonObject obj = arr.get(i).getAsJsonObject();
//        	JsonObject images = obj.getAsJsonObject("images");
////        	System.out.println(images.toString());
//                	
//        	// TODO qui dobbiamo fare una query decente.. cosi è una cosa veloce
//        	int size = images.getAsJsonArray("positive").size() + images.getAsJsonArray("negative").size();
//        	
//        	obj.remove("images");        	
//        	obj.addProperty("trainingSize", size);
//
//        }
//		response.getWriter().append(arr.toString());
//		//----------------------------------------------------------
//
////
		
		
	}
	
	
	private String sendMessage(String chat_id, String text) throws IOException{
		
		//https://api.telegram.org/bot000000000:AAAAa0aAA_aaA-Aaaa0A0Aa_a0aa0A0AAAA/sendmessage?text=Happy%20Little%20Clouds&chat_id=1111111111

		
		URL u = new URL("https://api.telegram.org/"+apikey+"/sendmessage?chat_id="+chat_id+"&text="+text);

		HttpURLConnection con = (HttpURLConnection) u.openConnection();
		con.setRequestMethod("GET");

		
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		
		//System.out.println(response.toString());
			
		return response.toString();
	}
	
	

}
