package dynamic_fps.impl.util;

import dynamic_fps.impl.GraphicsState;
import net.minecraft.client.AmbientOcclusionStatus;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.Options;
import net.minecraft.client.ParticleStatus;

/*
 * Helper for saving, overriding, and re-applying vanilla options.
 *
 * Different power states may be configured to use different graphics settings.
 */
public class OptionsHolder {
	private static CloudStatus cloudStatus;
	private static boolean graphicsStatus;
	private static AmbientOcclusionStatus ambientOcclusion;
	private static ParticleStatus particlesStatus;
	private static boolean entityShadows;

	/*
	 * Create an in-memory copy of current vanilla graphics options.
	 *
	 * This MUST be called while graphics options have not been changed yet.
	 */
	public static void copyOptions(Options options) {
		cloudStatus = options.renderClouds;
		graphicsStatus = options.fancyGraphics;
		ambientOcclusion = options.ambientOcclusion;
		particlesStatus = options.particles;
		entityShadows = options.entityShadows;
	}

	/*
	 * Apply or revert the graphics options for the specified graphics state.
	 */
	public static void applyOptions(Options options, GraphicsState state) {
		if (state == GraphicsState.DEFAULT) {
			options.renderClouds = cloudStatus;
			options.fancyGraphics = graphicsStatus;
			options.ambientOcclusion = ambientOcclusion;
			options.particles = particlesStatus;
			options.entityShadows = entityShadows;
		} else { // state == GraphicsState.REDUCED
			options.renderClouds = CloudStatus.OFF;
			options.particles = ParticleStatus.MINIMAL;
			options.entityShadows = false;

			if (state == GraphicsState.MINIMAL) {
				options.fancyGraphics = false;
				options.ambientOcclusion = AmbientOcclusionStatus.OFF;
			}
		}
	}
}
