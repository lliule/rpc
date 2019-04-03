package register;

import java.util.List;
import java.util.Map;

/**
 * 服务注册中心服务提供方
 */
public interface IRegisterCenter4Provider {

	/**
	 * 服务端将服务提供者信息注册到zk对应的节点下
	 * @param serviceMetaData
	 */
	void registerProvider(final List<ProviderService> serviceMetaData);

	Map<String, List<ProviderService>> getProviderServiceMap();
}
