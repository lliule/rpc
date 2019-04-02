package util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * FDateJsonSerializer Create on 2019/4/2
 * 格式化时间
 * @author leliu
 */
public class FDateJsonSerializer extends JsonSerializer<Date> {
	@Override
	public void serialize(Date value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		gen.writeString(value != null ? simpleDateFormat.format(value) : "null");
	}
}
