package register;

import java.util.List;
import java.util.Map;

/**
 * 服务注册中心服务消费方
 */
public interface IRegisterCenter4Consumer {

	/**
	 * 消费端初始化服务提供者信息本地缓存
	 */
	void initProviderMap();

	/**
	 * 消费端获取服务提供者信息
	 * @return
	 */
	Map<String, List<ProviderService>> getServiceMetaDataMap4Consume();

	void registerInvoker(final ConsumerService comsume);

}
