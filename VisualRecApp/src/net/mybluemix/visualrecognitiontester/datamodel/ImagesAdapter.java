package net.mybluemix.visualrecognitiontester.datamodel;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Serve per gestire parsing di Double come unsigned
 * @author Marco Dondio
 *
 */
public class ImagesAdapter implements JsonSerializer<Images>, JsonDeserializer<Images>{

	private Gson gson = new GsonBuilder().create();

	@Override
	public Images deserialize(JsonElement el, Type t, JsonDeserializationContext ctx) throws JsonParseException {
		Images i = new Images();
		
		
		JsonObject o = (JsonObject)el;

		JsonArray positive = o.get("positive").getAsJsonArray();
		JsonArray negative = o.get("negative").getAsJsonArray();
		

		
//		return gson.toJsonTree(list).getAsJsonArray();

		
//		= gson.fromJson(el.get, classOfT)
//				
//				((JsonObject) el).get("positive").;
//		
		
		
//				
//				obj = (JsonObject) ((JsonArray) dbEntry.getValue()).get(0);
//				serviceName = (String) dbEntry.getKey();
//				System.out.println("Service Name - " + serviceName);
//
//				obj = (JsonObject) obj.get("credentials");
//
//				user = obj.get("username").getAsString();
//				password = obj.get("password").getAsString();

				
		return i;
	}

	@Override
	public JsonElement serialize(Images images, Type t, JsonSerializationContext ctx) {
		JsonObject o = new JsonObject();
		
		
		JsonArray positive = new JsonArray();
		JsonArray negative = new JsonArray();
		
		// TODO
		
		o.add("positive", positive);
		o.add("negative", negative);
		
		return o;
	}
		



}
