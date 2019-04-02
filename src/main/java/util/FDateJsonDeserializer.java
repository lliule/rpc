package util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * FDateJsonDeserializer Create on 2019/4/2
 * 反序列化时间的格式
 * @author leliu
 */
public class FDateJsonDeserializer extends JsonDeserializer<Date> {
	@Override
	public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		String date = p.getText();
		if(StringUtils.isEmpty(date)) {
			return null;
		}
		if(StringUtils.isNumeric(date)) {
			return new Date(Long.valueOf(date));
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			return simpleDateFormat.parse(date);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
}
