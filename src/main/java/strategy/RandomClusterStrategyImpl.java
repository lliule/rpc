package strategy;

import org.apache.commons.lang3.RandomUtils;
import register.ProviderService;

import java.util.List;

/**
 * 随机算法实现负载均衡
 * @author leliu
 */
public class RandomClusterStrategyImpl implements ClusterStrategy{
	@Override
	public ProviderService select(List<ProviderService> providerServiceList) {
		int maxLen = providerServiceList.size();
		int index = RandomUtils.nextInt(0, maxLen - 1);
		return providerServiceList.get(index);
	}
}
