package register;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProviderService {
	private Class<?> serviceItf;
	private Object serviceObject;
	private String serverPort;
	private long timeout;
	private Object serviceProxyObject;
	private String appKey;
	private String groupName = "default";
	private int weight = 1;
	private int workerThreads = 10;
	private String serverIp;
}
