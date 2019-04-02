package serialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import util.FDateJsonDeserializer;
import util.FDateJsonSerializer;

import java.io.IOException;
import java.util.Date;

/**
 * JSONSerializer Create on 2019/4/2
 * 基于jackson的json序列化和反序列化
 * @author leliu
 */
public class JSONSerializer implements TSerialize{
	private static final ObjectMapper MAPPER = new ObjectMapper();
	static {
		MAPPER.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
		MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
		MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		SimpleModule module = new SimpleModule("DateTimeModule", Version.unknownVersion());
		module.addSerializer(Date.class, new FDateJsonSerializer());
		module.addDeserializer(Date.class, new FDateJsonDeserializer());
		MAPPER.registerModule(module);
	}
	public <T> byte[] serialize(T obj) {
		if(obj == null) {
			return new byte[0];
		}

		try {
			String json = MAPPER.writeValueAsString(obj);
			return json.getBytes();
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}

	}

	public <T> T deserialize(byte[] data, Class<T> clazz) {
		try {
			return MAPPER.readValue(data, clazz);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
