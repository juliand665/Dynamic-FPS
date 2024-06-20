package dynamic_fps.impl.config;

import dynamic_fps.impl.config.option.IdleCondition;

public class IdleConfig {
	private int timeout;
	private IdleCondition condition;

	public int timeout() {
		return this.timeout;
	}

	public void setTimeout(int value) {
		this.timeout = value;
	}

	public IdleCondition condition() {
		return this.condition;
	}

	public void setCondition(IdleCondition value) {
		this.condition = value;
	}
}
