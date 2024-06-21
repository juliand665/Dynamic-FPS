package dynamic_fps.impl.config;

public class VolumeTransitionConfig {
	private float up;
	private float down;

	private static final float IMMEDIATE = 10.0f;

	protected VolumeTransitionConfig(float up, float down) {
		this.up = up;
		this.down = down;
	}

	public float getUp() {
		if (this.up == -1) {
			return IMMEDIATE;
		} else {
			return this.up;
		}
	}

	public void setUp(float value) {
		if (value >= IMMEDIATE) {
			this.up = -1.0f;
		} else {
			this.up = value;
		}
	}

	public float getDown() {
		if (this.down == -1) {
			return IMMEDIATE;
		} else {
			return this.down;
		}
	}

	public void setDown(float value) {
		if (value >= IMMEDIATE) {
			this.down = -1.0f;
		} else {
			this.down = value;
		}
	}

	public boolean isActive() {
		return this.up != -1.0f || this.down != -1.0f;
	}
}
