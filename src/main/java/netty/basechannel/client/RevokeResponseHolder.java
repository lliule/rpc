package netty.basechannel.client;

import com.google.common.collect.Maps;
import netty.basechannel.AresResponse;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 定义保存及操作返回结果的数据容器类
 * @author leliu
 */
public class RevokeResponseHolder {

	/**
	 * 服务返回结果map
	 */
	private static final Map<String, AresResponseWrapper> responseMap = Maps.newConcurrentMap();

	private static final ExecutorService removeExpireKeyExecutor = Executors.newSingleThreadExecutor();

	/**
	 * 删除超时未获取到结果的key,防止内存泄漏
	 */
	static  {
		removeExpireKeyExecutor.execute(() -> {
			while (true) {
				for (Map.Entry<String, AresResponseWrapper> enty : responseMap.entrySet()) {
					boolean expire = enty.getValue().isExpire();
					if(expire) {
						responseMap.remove(enty.getKey());
					}
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	/**
	 * 初始化返回结果容器
	 * @param requestUniqueKey 唯一标识本次调用
	 */
	public static void initResponseData(String requestUniqueKey) {
		responseMap.put(requestUniqueKey, AresResponseWrapper.of());
	}

	/**
	 * 将netty调用异步返回结果放入阻塞队列
	 * @param response
	 */
	public static void putResultValue(AresResponse response) {
		long currentTimeMillis = System.currentTimeMillis();
		AresResponseWrapper aresReponseWrapper = responseMap.get(response.getUniqueKey());

		aresReponseWrapper.setResponseTime(currentTimeMillis);
		aresReponseWrapper.getResponseQueue().add(response);
		responseMap.put(response.getUniqueKey(), aresReponseWrapper);
	}

	/**
	 * 从阻塞队列获取netty 异步返回的结果值
	 * @param requestuniqueKey
	 * @param timeout
	 * @return
	 */
	public static AresResponse getValue(String requestuniqueKey, long timeout) {
		AresResponseWrapper responseWrapper = responseMap.get(requestuniqueKey);

		try {
			return responseWrapper.getResponseQueue().poll(timeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} finally {
			responseMap.remove(requestuniqueKey);
		}
	}

}
