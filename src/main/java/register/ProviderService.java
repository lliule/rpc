package register;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;

@Getter
@Setter
public class ProviderService {
	private Class<?> serviceItf;
	private Object serviceObject;
	private int serverPort;
	private long timeout;
	private Object serviceProxyObject;
	private String appKey;
	private String groupName = "default";
	private int weight = 1;
	private int workerThreads = 10;
	private String serverIp;
	private Method serviceMethod;

	public ProviderService copy() {
		return this;
	}
}
