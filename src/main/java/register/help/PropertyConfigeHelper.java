package register.help;

import com.google.common.collect.Lists;

/**
 * @author leliu
 */
public class PropertyConfigeHelper {

	private static final int zkConnectionTimeout = 3000;
	private static final int zkSessionTimeout = 2000;
	private static final String zkService = "106.12.204.20";
	private static final String appName = "leliu";

	public static int getZkConnectionTimeout() {
		return zkConnectionTimeout;
	}

	public static String getZkService() {
		return zkService;
	}

	public static String getAppName() {
		return appName;
	}

	public static int getZkSessionTimeout() {
		return zkSessionTimeout;
	}
}
