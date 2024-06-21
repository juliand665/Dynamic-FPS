package dynamic_fps.impl.config;

public class VolumeTransitionConfig {
	private float up;
	private float down;

	protected VolumeTransitionConfig(float up, float down) {
		this.up = up;
		this.down = down;
	}

	public float getUp() {
		return this.up;
	}

	public void setUp(float value) {
		this.up = value;
	}

	public float getDown() {
		return this.down;
	}

	public void setDown(float value) {
		this.down = value;
	}

	public boolean isActive() {
		return this.up != 100.0f || this.down != 100.0f;
	}
}
