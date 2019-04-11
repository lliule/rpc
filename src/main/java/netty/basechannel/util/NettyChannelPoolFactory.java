package netty.basechannel.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import netty.basechannel.AresResponse;
import netty.basechannel.NettyDecoderHander;
import netty.basechannel.NettyEncoderHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import register.ProviderService;
import register.help.PropertyConfigeHelper;
import serialize.SerializeType;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;

/**
 * channel连接池工厂，复用channel，为每一个服务提供者地址，预先生成一个保存channel的阻塞队列
 * @author dana
 */
public class NettyChannelPoolFactory {

    private static final Logger logger  = LoggerFactory.getLogger(NettyChannelPoolFactory.class);

    private static final NettyChannelPoolFactory channelPoolFactory = new NettyChannelPoolFactory();

    /**
     * Key为服务提供者地址，value为netty channel阻塞队列
     */
    private static final Map<InetSocketAddress, ArrayBlockingQueue<Channel>> channelPoolMap = Maps.newConcurrentMap();

    //初始化netty channel 阻塞队列的长度，该值为可配置信息
    private static final int channelConnetSize = PropertyConfigeHelper.getChannelConnectSize();

    private static final SerializeType serializeType = PropertyConfigeHelper.getSerializeType();

    /**
     * 服务提供者列表
     */
    private List<ProviderService> serviceMetaDataList = Lists.newArrayList();

    private NettyChannelPoolFactory() {
    }

    /**
     * 初始化netty channel 链接队列
     * @param providerMap
     */
    public void initChannelPoolFactory(Map<String, List<ProviderService>> providerMap){
        Collection<List<ProviderService>> collectionServiceMetaDataList = providerMap.values();
        for (List<ProviderService> providerServiceList : collectionServiceMetaDataList) {
            if(CollectionUtils.isEmpty(providerServiceList)) {
                continue;
            }
            serviceMetaDataList.addAll(providerServiceList);
        }

        HashSet<InetSocketAddress> socketAddressHashSet = Sets.newHashSet();
        for (ProviderService providerService : serviceMetaDataList) {
            String serviceIp = providerService.getServerIp();
            int servicePort = providerService.getServerPort();
            InetSocketAddress socketAddress = new InetSocketAddress(serviceIp, servicePort);
            socketAddressHashSet.add(socketAddress);
        }

        for (InetSocketAddress socketAddress : socketAddressHashSet) {
            int realChannelConnetSize = 0;
            while (realChannelConnetSize < channelConnetSize) {
                Channel channel = null;
                while (channel == null) {
                    // 若不存在，则注册新的netty channel
                    channel = regesterChannel(socketAddress);
                }
                // 计数器，初始化的时候存入阻塞队列的netty channel 个数不超过channelConnectSize
                realChannelConnetSize ++;

                // 将新注册的netty channel 存入阻塞队列 channelArrayBlockingQueue
                // 并经阻塞队列 channelArrayBlockingQueue 作为 value存入 channelPoolMap

                ArrayBlockingQueue<Channel> channelArrayBlockingQueue = channelPoolMap.get(socketAddress);
                if(channelArrayBlockingQueue == null) {
                    channelArrayBlockingQueue = new ArrayBlockingQueue<Channel>(channelConnetSize);
                    channelPoolMap.put(socketAddress, channelArrayBlockingQueue);
                }

                channelArrayBlockingQueue.offer(channel);

            }
        }
    }

    /**
     * 根据服务提供者地址获取对应的 channel阻塞队列
     * @param socketAddress
     * @return
     */
    public ArrayBlockingQueue<Channel> acquire(InetSocketAddress socketAddress) {
        return channelPoolMap.get(socketAddress);
    }

    /**
     * channel 使用完毕后，会受到阻塞队列arrayBlockingQueue
     * @param arrayBlockingQueue arrayBlockingQueue
     * @param channel channel
     * @param socketAddress socketAddress
     */
    public void release(ArrayBlockingQueue<Channel> arrayBlockingQueue, Channel channel, InetSocketAddress socketAddress){
        if(arrayBlockingQueue == null) {
            return;
        }

        // 回收之前先检查channel是否可用，不可用的话，重新注册一个.放入阻塞队列
        if(channel == null || !channel.isActive() || !channel.isOpen() || !channel.isWritable()) {
            if(channel != null) {
                channel.deregister().syncUninterruptibly().awaitUninterruptibly();
                channel.closeFuture().syncUninterruptibly().awaitUninterruptibly();
            }

            Channel newChannel = null;
            while (newChannel == null) {
                logger.debug("=========register new channel=========");
                newChannel = registerChannel(socketAddress);
            }

            arrayBlockingQueue.offer(newChannel);
            return;
        }

        arrayBlockingQueue.offer(channel);

    }

    public Channel registerChannel(InetSocketAddress socketAddress) {
        NioEventLoopGroup group = new NioEventLoopGroup(10);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.remoteAddress(socketAddress);

        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new NettyEncoderHandler(serializeType));
                        ch.pipeline().addLast(new NettyDecoderHander(AresResponse.class, serializeType));
                    ch.pipeline().addLast(new NettyClientInvokeHandler());
                    }
                });

        try {
            ChannelFuture channelFuture = bootstrap.connect().sync();
            Channel newChannel = channelFuture.channel();
            CountDownLatch countDownLatch = new CountDownLatch(1);

            ArrayList<Boolean> isSuccessHolder = Lists.newArrayListWithCapacity(1);
            channelFuture.addListener((future -> {
                if(future.isSuccess()) {
                    isSuccessHolder.add(Boolean.TRUE);
                } else {
                    // 若channel 建立失败，保存建立失败的标记
                    future.cause().printStackTrace();
                    isSuccessHolder.add(Boolean.FALSE);
                }
                countDownLatch.countDown();
            }));

            countDownLatch.await();

            // 如果channel建立成功，返回新建的channel
            if (isSuccessHolder.get(0)) {
                return newChannel;
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static NettyChannelPoolFactory channelPoolFactory () {
        return channelPoolFactory;
    }


}
