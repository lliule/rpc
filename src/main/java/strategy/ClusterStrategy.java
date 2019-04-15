package strategy;

import register.ProviderService;

import java.util.List;

public interface ClusterStrategy {

	ProviderService select(List<ProviderService> providerServiceList);

}
