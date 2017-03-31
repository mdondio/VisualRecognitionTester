package net.mybluemix.visualrecognitiontester.datamodel;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

/**
 * Serve per gestire parsing di date con valori nulli
 * @author Marco Dondio
 *
 */
public class DateAdapter implements JsonDeserializer<Date>{

	// http://stackoverflow.com/questions/289311/output-rfc-3339-timestamp-in-java
	DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
	@Override
	public Date deserialize(JsonElement json, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
            try {
                return df.parse(json.getAsString());
            } catch (ParseException e) {
                return null;
            }
	}
}
