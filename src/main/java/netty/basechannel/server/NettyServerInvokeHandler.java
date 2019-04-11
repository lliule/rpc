package netty.basechannel.server;

import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import netty.basechannel.AresRequest;
import netty.basechannel.AresResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import register.ProviderService;
import register.RegisterCenter;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 获取java请求对象确定服务提供者接口和方法，然后用过反射发起调用
 * @author leliu
 */
public class NettyServerInvokeHandler extends SimpleChannelInboundHandler<AresRequest> {

	private static final Logger logger = LoggerFactory.getLogger(NettyServerInvokeHandler.class);

	/**
	 * 服务端限流
 	 */
	private static final Map<String, Semaphore> serviceKeySemaphoreMap = Maps.newConcurrentMap();

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, AresRequest request) {
		if(ctx.channel().isWritable()) {
			// 调用对象里获取服务提供者信息
			ProviderService providerService = request.getProviderService();
			long invokeTimeout = request.getInvokeTimeout();
			String invokedMethodName = request.getInvokedMethodName();
			// 根据方法民称定位到具体某一个服务提供者
			String serviceKey = providerService.getServiceItf().getName();
			//获取限流工具类
			int workerThread = providerService.getWorkerThreads();
			Semaphore semaphore = serviceKeySemaphoreMap.get(serviceKey);
			// 初始化留空基础设施
			if(semaphore == null ){
				synchronized (serviceKeySemaphoreMap) {
					semaphore = serviceKeySemaphoreMap.get(serviceKey);
					if(semaphore == null) {
						semaphore  = new Semaphore(workerThread);
						serviceKeySemaphoreMap.put(serviceKey, semaphore);
					}
				}
			}

			// 获取注册中心服务
			RegisterCenter registerCenter = RegisterCenter.singleton();
			List<ProviderService> localProviderCaches = registerCenter.getProviderServiceMap().get(serviceKey);

			ProviderService localProviderCache = Collections2.filter(localProviderCaches, (input) -> StringUtils.equals(input.getServiceMethod().getName(), invokedMethodName))
					.iterator().next();

			Object serviceObject = localProviderCache.getServiceObject();
			Method method = localProviderCache.getServiceMethod();
			Object result = null;
			boolean acquire = false;

			// 利用semaphore实现限流
			try {
				acquire = semaphore.tryAcquire(invokeTimeout, TimeUnit.MILLISECONDS);
				if(acquire) {
					result = method.invoke(serviceObject, request.getArgs());
				}
			} catch (Exception e) {
				result = e;
			} finally {
				if(acquire){
					semaphore.release();
				}
			}
			// 根据与服务调用结果组装调用返回对象
			AresResponse response = new AresResponse();
			response.setInvokeTimeout(invokeTimeout);
			response.setUniqueKey(request.getUniqueKey());
			response.setResult(result);
			ctx.writeAndFlush(response);
		} else {
			logger.error(" ----------------channel closed!---------------------");
		}
		

	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}
