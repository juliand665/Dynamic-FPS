package dynamic_fps.impl.util;

import dynamic_fps.impl.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Logging {
	private static final Logger logger = LoggerFactory.getLogger(Constants.MOD_ID);

	public static Logger getLogger() {
		return logger;
	}
}
