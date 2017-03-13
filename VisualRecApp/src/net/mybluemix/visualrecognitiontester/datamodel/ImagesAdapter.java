package net.mybluemix.visualrecognitiontester.datamodel;

import java.lang.reflect.Type;
import java.util.ArrayList;
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

	private Gson gsonLongToUnsignedString = new GsonBuilder().registerTypeAdapter(Long.class, new JsonSerializer<Long>(){

		@Override
		public JsonElement serialize(Long l, Type t, JsonSerializationContext arg2) {
			return gsonLongToUnsignedString.toJsonTree(Long.toUnsignedString(l));
		}})
			.create();

	@Override
	public Images deserialize(JsonElement el, Type t, JsonDeserializationContext ctx) throws JsonParseException {
		Images img = new Images();
		
		
		JsonObject o = (JsonObject)el;

		JsonArray positive = o.get("positive").getAsJsonArray();
		JsonArray negative = o.get("negative").getAsJsonArray();

		List<Long> pos = new ArrayList<Long>();
		List<Long> neg = new ArrayList<Long>();
		
		for(int j = 0; j < positive.size();j++)
			pos.add(Long.parseUnsignedLong(positive.get(j).getAsString()));

		for(int j = 0; j < negative.size();j++)
			neg.add(Long.parseUnsignedLong(negative.get(j).getAsString()));
		
		img.setPositives(pos);
		img.setNegatives(neg);
		
		return img;
	}

	@Override
	public JsonElement serialize(Images images, Type t, JsonSerializationContext ctx) {
		JsonObject o = new JsonObject();
		
		// we use custom serializer to transform
		JsonArray positive =  gsonLongToUnsignedString.toJsonTree(images.getPositives()).getAsJsonArray();
		JsonArray negative =  gsonLongToUnsignedString.toJsonTree(images.getNegatives()).getAsJsonArray();
		
		o.add("positive", positive);
		o.add("negative", negative);
		
		System.out.println("serialized: \n" + o);

		
		return o;
	}
}
