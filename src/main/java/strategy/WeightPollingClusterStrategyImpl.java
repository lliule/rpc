package strategy;

import com.google.common.collect.Lists;
import register.ProviderService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author leliu
 */
public class WeightPollingClusterStrategyImpl implements ClusterStrategy{

	private int index = 0;

	private Lock lock = new ReentrantLock();


	@Override
	public ProviderService select(List<ProviderService> providerServiceList) {
		ProviderService service = null;

		try {
			lock.tryLock(10, TimeUnit.MILLISECONDS);
			ArrayList<ProviderService> providerList = Lists.newArrayList();

			for (ProviderService providerService : providerServiceList) {
				int weight = providerService.getWeight();
				for (int i = 0; i < weight; i++) {
					providerList.add(providerService.copy());
				}
			}

			if(index >= providerList.size()) {
				index = 0;
			}

			service = providerList.get(index);
			index ++;
			return service;

		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
		// 兜底
		return providerServiceList.get(0);

	}
}
