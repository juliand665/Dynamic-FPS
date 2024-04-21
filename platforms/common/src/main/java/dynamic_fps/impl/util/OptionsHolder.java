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
	private static double entityDistance;

	/*
	 * Create an in-memory copy of current vanilla graphics options.
	 *
	 * This MUST be called while graphics options have not been changed yet.
	 */
	public static void copyOptions(Options options) {
		cloudStatus = options.getCloudsType();
		graphicsStatus = options.graphicsMode().get();
		ambientOcclusion = options.ambientOcclusion().get();
		particlesStatus = options.particles().get();
		entityShadows = options.entityShadows().get();
		entityDistance = options.entityDistanceScaling().get();
	}

	/*
	 * Apply or revert the graphics options for the specified graphics state.
	 */
	public static void applyOptions(Options options, GraphicsState state) {
		if (state == GraphicsState.DEFAULT) {
			options.cloudStatus().set(cloudStatus);
			options.graphicsMode().set(graphicsStatus);
			options.ambientOcclusion().set(ambientOcclusion);
			options.particles().set(particlesStatus);
			options.entityShadows().set(entityShadows);
			options.entityDistanceScaling().set(entityDistance);
		} else { // state == GraphicsState.REDUCED
			options.cloudStatus().set(CloudStatus.OFF);
			options.particles().set(ParticleStatus.MINIMAL);
			options.entityShadows().set(false);
			options.entityDistanceScaling().set(0.5);

			if (state == GraphicsState.MINIMAL) {
				options.graphicsMode().set(GraphicsStatus.FAST);
				options.ambientOcclusion().set(AmbientOcclusionStatus.OFF);
			}
		}
	}
}
