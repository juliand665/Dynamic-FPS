package dynamic_fps.impl.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dynamic_fps.impl.DynamicFPSMod;

public class Logging {
	private static final Logger logger = LoggerFactory.getLogger(DynamicFPSMod.MOD_ID);

	public static Logger getLogger() {
		return logger;
	}
}
