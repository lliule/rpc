package strategy;

import register.ProviderService;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 轮询算大
 * @author leliu
 */
public class PollingClusterStrategyImpl implements ClusterStrategy {

	private int index = 0;
	private Lock lock = new ReentrantLock();

	@Override
	public ProviderService select(List<ProviderService> providerServiceList) {


		ProviderService service = null;

		try {
			lock.tryLock(10, TimeUnit.MILLISECONDS);
			// 若技术大于服务提供者个数，将计数器归0
			if(index >= providerServiceList.size()) {
				index = 0;
			}

			service = providerServiceList.get(index);
			index++;

		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}

		// 保证健壮性，若未取到，则直接取第1个
		if(service == null) {
			service = providerServiceList.get(0);
		}

		return service;

	}
}
