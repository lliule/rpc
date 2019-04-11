package netty.basechannel;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author leliu
 */
@Setter
@Getter
public class AresResponse {

	private long invokeTimeout;

	private String uniqueKey;
	private Object result;
}
