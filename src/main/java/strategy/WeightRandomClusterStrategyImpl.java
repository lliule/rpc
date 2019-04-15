package strategy;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.RandomUtils;
import register.ProviderService;

import java.util.ArrayList;
import java.util.List;

/**
 * 加权随机算法
 * @author leliu
 */
public class WeightRandomClusterStrategyImpl implements ClusterStrategy {
	@Override
	public ProviderService select(List<ProviderService> providerServiceList) {
		ArrayList<ProviderService> providerList = Lists.newArrayList();

		// 存放加权后的服务列表，权重越高，获取服务概率越大
		for (ProviderService providerService : providerServiceList) {
			int weight = providerService.getWeight();
			for (int i = 0; i < weight; i++) {
				providerList.add(providerService.copy());
			}
		}

		int maxLen = providerList.size();

		int index = RandomUtils.nextInt(0, maxLen - 1);

		return providerList.get(index);
	}
}
