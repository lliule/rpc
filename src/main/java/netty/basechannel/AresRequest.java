package netty.basechannel;

import lombok.Getter;
import lombok.Setter;
import register.ProviderService;

/**
 * 解码对象
 * @author leliu
 */
@Setter
@Getter
public class AresRequest {
	private ProviderService providerService;
	private long invokeTimeout;
	private String invokedMethodName;
	private Object args;

	private String uniqueKey;
}
