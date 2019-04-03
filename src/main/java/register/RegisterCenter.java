package register;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import register.help.PropertyConfigeHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * 注册中心
 * @author leliu
 */
public class RegisterCenter implements IRegisterCenter4Consumer, IRegisterCenter4Provider{
	private static RegisterCenter registerCenter = new RegisterCenter();
	/**
	 * 服务提供者列表： key - 服务提供者接口， value: 服务提供者服务方法列表
	 */
	private static final Map<String, List<ProviderService>> providerServiceMap = Maps.newConcurrentMap();
	/**
	 * 服务端ZK服务元信息，选择服务（第一次直接从zk拉取，后续由zk的监听机制主动更新)
	 */
	private static final Map<String, List<ProviderService>> serviceMetaDataMap4Consume = Maps.newConcurrentMap();

	private static final String ZK_SERVICE = PropertyConfigeHelper.getZkService();
	private static final int ZK_CONNECTIONTIMEOUT = PropertyConfigeHelper.getZkConnectionTimeout();
	private static final int ZK_SESSION_TIME_OUT = PropertyConfigeHelper.getZkSessionTimeout();
	private static String ROOT_PATH = "/config_register/" + PropertyConfigeHelper.getAppName();
	private static String PROVIDER_TYPE = "/provider";
	private static String CONSUME_TYPE  = "/consumer";
	private static String LOCAL_IP = "localhost";
	private static volatile ZkClient zkClient = null;

	private RegisterCenter () {}

	public static RegisterCenter singleton() {
		return registerCenter;
	}

	public void initProviderMap() {
		if(MapUtils.isEmpty(serviceMetaDataMap4Consume)) {
			serviceMetaDataMap4Consume.putAll(fetchOrUpdateServiceMetaData());
		}
	}

	private Map<String, List<ProviderService>> fetchOrUpdateServiceMetaData() {
		ConcurrentMap<String, List<ProviderService>> providerServiceMap = Maps.newConcurrentMap();

		synchronized (RegisterCenter.class) {
			if(zkClient == null) {
				zkClient = new ZkClient(ZK_SERVICE, ZK_CONNECTIONTIMEOUT, ZK_SESSION_TIME_OUT, new SerializableSerializer());
			}
		}
		// 从zk获取服务提供者列表
		String providePath = ROOT_PATH;
		List<String> providerServices = zkClient.getChildren(providePath);
		for (String serviceName : providerServices) {
			String servicePath = providePath + "/" + serviceName + PROVIDER_TYPE;
			List<String> ipPathList = zkClient.getChildren(servicePath);
			for (String ipPath : ipPathList) {
				String serverIp = StringUtils.split(ipPath, "|")[0];
				String serverPort = StringUtils.split(ipPath, "|")[1];
				List<ProviderService> providerServiceList = providerServiceMap.get(serviceName);
				if(providerServiceList == null) {
					providerServiceList = Lists.newArrayList();
				}
				ProviderService providerService = new ProviderService();

				try {
					providerService.setServiceItf(ClassUtils.getClass(serviceName));
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
				providerService.setServerIp(serverIp);
				providerService.setServerPort(serverPort);
				providerServiceList.add(providerService);
				providerServiceMap.put(serviceName, providerServiceList);
			}
			// 监听注册服务的变化，同事更新数据到本地缓存
			zkClient.subscribeChildChanges(servicePath, new IZkChildListener() {
				public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
					if(currentChilds == null) {
						currentChilds = Lists.newArrayList();
					}

					currentChilds = Lists.newArrayList(Lists.transform(currentChilds, new Function<String, String>() {
						@Nullable
						public String apply(@Nullable String s) {
							return StringUtils.split(s, "|")[0];
						}
					}));
					refreshServiceMetaDataMap(currentChilds);
				}
			});
		}
		return providerServiceMap;
	}

	private void refreshServiceMetaDataMap(List<String> serviceIpList) {
		if(serviceIpList == null) {
			serviceIpList = Lists.newArrayList();
		}

		HashMap<String, List<ProviderService>> currentServiceMetaDataMap = Maps.newHashMap();
		for (Map.Entry<String, List<ProviderService>> entry : serviceMetaDataMap4Consume.entrySet()) {
			String serviceItfKey = entry.getKey();
			List<ProviderService> serviceList = entry.getValue();
			List<ProviderService> providerServiceList = currentServiceMetaDataMap.get(serviceItfKey);
			if(providerServiceList == null) {
				providerServiceList = Lists.newArrayList();
			}

			for (ProviderService serviceMetaData : serviceList) {
				if(serviceIpList.contains(serviceMetaData.getServerIp())) {
					providerServiceList.add(serviceMetaData);
				}
			}

			currentServiceMetaDataMap.put(serviceItfKey, providerServiceList);
		}

		serviceMetaDataMap4Consume.putAll(currentServiceMetaDataMap);
		ObjectMapper mapper = new ObjectMapper();
		try {
			System.out.println("serviceMetaDataMap4Consume: " + mapper.writeValueAsString(serviceMetaDataMap4Consume));
		} catch (JsonProcessingException e) {
		}

	}

	public Map<String, List<ProviderService>> getServiceMetaDataMap4Consume() {
		return serviceMetaDataMap4Consume;
	}

	public void registerInvoker(ConsumerService comsume) {
		if(comsume == null) {
			return ;
		}

		synchronized (RegisterCenter.class) {
			if(zkClient == null) {
				zkClient = new ZkClient(ZK_SERVICE, ZK_CONNECTIONTIMEOUT, ZK_SESSION_TIME_OUT, new SerializableSerializer());
			}
			if(!zkClient.exists(ROOT_PATH)) {
				zkClient.createPersistent(ROOT_PATH, true);
			}

			if(!zkClient.exists(ROOT_PATH)) {
				zkClient.createPersistent(ROOT_PATH);
			}
			String serviceNode = comsume.getServiceItf().getName();
			String servicePath = ROOT_PATH +"/" + serviceNode + CONSUME_TYPE;
			if(!zkClient.exists(servicePath)) {
				zkClient.createPersistent(servicePath);
			}
			// 创建当前服务器节点
			String localIp = LOCAL_IP;
			String currentServiceIpNode = servicePath + "/" + localIp;
			if(!zkClient.exists(currentServiceIpNode)){
				// !临时节点
				zkClient.createEphemeral(currentServiceIpNode);
			}
		}
	}

	public void registerProvider(List<ProviderService> serviceMetaData) {
		if(CollectionUtils.isEmpty(serviceMetaData)) {
			return ;
		}
		// 连接zk，注册服务
		synchronized (RegisterCenter.class) {
			for (ProviderService providerService : serviceMetaData) {
				String serviceItfKey = providerService.getServiceItf().getName();
				List<ProviderService> providerServices = providerServiceMap.get(serviceItfKey);
				if(providerServices == null) {
					providerServices = Lists.newArrayList();
				}
				providerServices.add(providerService);
				providerServiceMap.put(serviceItfKey, providerServices);
			}
			if(zkClient == null) {
				zkClient = new ZkClient(ZK_SERVICE, ZK_CONNECTIONTIMEOUT, ZK_SESSION_TIME_OUT, new SerializableSerializer());
			}
			// 创建zk命名空间/当前部署应用App
			if(!zkClient.exists(ROOT_PATH)) {
				zkClient.createPersistent(ROOT_PATH, true);
			}
			// 创建服务提供者节点
			if(!zkClient.exists(ROOT_PATH)) {
				zkClient.createPersistent(ROOT_PATH);
			}

			for (Map.Entry<String, List<ProviderService>> entry : providerServiceMap.entrySet()) {
				String serviceNode = entry.getKey();
				String servicePath = ROOT_PATH + "/" + serviceNode + PROVIDER_TYPE;
				if(!zkClient.exists(servicePath)) {
					zkClient.createPersistent(servicePath, true);
				}
				// 创建当前服务器临时节点
				String serverPort = entry.getValue().get(0).getServerPort();
				String currentServiceIpNode = servicePath + "/" + LOCAL_IP + "|" + serverPort;
				if(!zkClient.exists(currentServiceIpNode)) {
					zkClient.createEphemeral(currentServiceIpNode);
				}
				// 监听注册服务的变化，同事更新数据到本地缓存
				zkClient.subscribeChildChanges(servicePath, new IZkChildListener() {
					public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
						if(CollectionUtils.isEmpty(currentChilds)) {
							currentChilds = Lists.newArrayList();
						}
						// 存活的服务ip列表
						ArrayList<String> activityServiceIpList = Lists.newArrayList(Lists.transform(currentChilds, new Function<String, String>() {
							@Nullable
							public String apply(@Nullable String s) {
								return StringUtils.split(s, "|")[0];
							}
						}));
						refreshActivityService(activityServiceIpList);
					}
				});
			}
		}
	}

	/**
	 * 利用ZK自动刷新当前存活服务提供者列表数据
	 * @param activityServiceIpList 存活服务
	 */
	private void refreshActivityService(ArrayList<String> activityServiceIpList) {
		if(activityServiceIpList == null) {
			activityServiceIpList = Lists.newArrayList();
		}
		HashMap<String, List<ProviderService>> currentServiceMetaDataMap = Maps.newHashMap();

		for (Map.Entry<String, List<ProviderService>> entry : providerServiceMap.entrySet()) {
			String key = entry.getKey();
			List<ProviderService> providerServices = entry.getValue();

			List<ProviderService> serviceList = currentServiceMetaDataMap.get(key);
			if(serviceList == null) {
				serviceList = Lists.newArrayList();
			}

			for (ProviderService providerService : providerServices) {
				if(activityServiceIpList.contains(providerService.getServerIp())) {
					serviceList.add(providerService);
				}
			}
			currentServiceMetaDataMap.put(key, serviceList);
		}
		providerServiceMap.putAll(currentServiceMetaDataMap);
	}

	public Map<String, List<ProviderService>> getProviderServiceMap() {
		return providerServiceMap;
	}



}
