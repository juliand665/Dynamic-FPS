package dynamic_fps.impl.util;

import dynamic_fps.impl.GraphicsState;
import net.minecraft.client.AmbientOcclusionStatus;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Options;
import net.minecraft.client.ParticleStatus;

/*
 * Helper for saving, overriding, and re-applying vanilla options.
 *
 * Different power states may be configured to use different graphics settings.
 */
public class OptionsHolder {
	private static CloudStatus cloudStatus;
	private static GraphicsStatus graphicsStatus;
	private static AmbientOcclusionStatus ambientOcclusion;
	private static ParticleStatus particlesStatus;
	private static boolean entityShadows;
	private static float entityDistance;

	/*
	 * Create an in-memory copy of current vanilla graphics options.
	 *
	 * This MUST be called while graphics options have not been changed yet.
	 */
	public static void copyOptions(Options options) {
		cloudStatus = options.renderClouds;
		graphicsStatus = options.graphicsMode;
		ambientOcclusion = options.ambientOcclusion;
		particlesStatus = options.particles;
		entityShadows = options.entityShadows;
		entityDistance = options.entityDistanceScaling;
	}

	/*
	 * Apply or revert the graphics options for the specified graphics state.
	 */
	public static void applyOptions(Options options, GraphicsState state) {
		if (state == GraphicsState.DEFAULT) {
			options.renderClouds = cloudStatus;
			options.graphicsMode = graphicsStatus;
			options.ambientOcclusion = ambientOcclusion;
			options.particles = particlesStatus;
			options.entityShadows = entityShadows;
			options.entityDistanceScaling = entityDistance;
		} else { // state == GraphicsState.REDUCED
			options.renderClouds = CloudStatus.OFF;
			options.particles = ParticleStatus.MINIMAL;
			options.entityShadows = false;
			options.entityDistanceScaling = 0.5f;

			if (state == GraphicsState.MINIMAL) {
				options.graphicsMode = GraphicsStatus.FAST;
				options.ambientOcclusion = AmbientOcclusionStatus.OFF;
			}
		}
	}
}
