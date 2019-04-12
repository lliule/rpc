package netty.basechannel.client;

import netty.basechannel.AresResponse;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by dana on 2019/4/11.
 *
 * @author dana
 */
public class AresResponseWrapper {

    /**
     * 存储返回结果的阻塞队列
     */
    private BlockingQueue<AresResponse> responseQueue = new ArrayBlockingQueue<AresResponse>(1);

    /**
     * 返回时间
     */
    private long responseTime;

    /**
     * 计算该返回加过是否已经过期
     * @return
     */
    public boolean isExpire() {
        AresResponse response = responseQueue.peek();
        if(response == null) {
            return false;
        }

        long timeout = response.getInvokeTimeout();
        if(System.currentTimeMillis() - responseTime > timeout) {
            return true;
        }
        return false;
    }

    public static AresResponseWrapper of() {
        return new AresResponseWrapper();
    }

    public long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
    }

    public BlockingQueue<AresResponse> getResponseQueue() {
        return responseQueue;
    }

    public void setResponseQueue(BlockingQueue<AresResponse> responseQueue) {
        this.responseQueue = responseQueue;
    }
}
