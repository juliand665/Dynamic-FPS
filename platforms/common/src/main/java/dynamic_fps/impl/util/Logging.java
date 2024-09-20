package dynamic_fps.impl.util;

import dynamic_fps.impl.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Logging {
	private static final Logger logger = LogManager.getLogger(Constants.MOD_ID);

	public static Logger getLogger() {
		return logger;
	}
}
