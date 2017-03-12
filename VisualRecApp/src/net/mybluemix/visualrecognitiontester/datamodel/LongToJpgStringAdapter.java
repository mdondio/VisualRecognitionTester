package net.mybluemix.visualrecognitiontester.datamodel;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Serve per gestire parsing di Double come unsigned
 * @author Marco Dondio
 *
 */
public class LongToJpgStringAdapter implements JsonSerializer<Long>, JsonDeserializer<Long>{

	private Gson gson = new Gson();
		
	@Override
	public Long deserialize(JsonElement el, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {

	return	Long.parseUnsignedLong(el.getAsString());
	}

	@Override
	public JsonElement serialize(Long l, Type t, JsonSerializationContext ctx) {
		
		return gson.toJsonTree(Long.toUnsignedString(l) + ".jpg");
	}



}
