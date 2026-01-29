package dynamic_fps.impl.feature.state;

import dynamic_fps.impl.config.option.GraphicsState;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.GraphicsPreset;
import net.minecraft.client.Options;
import net.minecraft.server.level.ParticleStatus;

/*
 * Helper for saving, overriding, and re-applying vanilla options.
 *
 * Different power states may be configured to use different graphics settings.
 */
public class OptionHolder {
	private static int biomeBlendRadius;
	private static int cloudRange;
	private static CloudStatus cloudStatus;
	private static GraphicsPreset graphicsStatus;
	private static boolean ambientOcclusion;
	private static ParticleStatus particlesStatus;
	private static boolean entityShadows;
	private static double entityDistance;
	private static boolean cutoutLeaves;
	private static boolean improvedTransparency;
	private static int weatherRadius;

	/*
	 * Create an in-memory copy of current vanilla graphics options.
	 *
	 * This MUST be called while graphics options have not been changed yet.
	 */
	public static void copyOptions(Options options) {
		biomeBlendRadius = options.biomeBlendRadius().get();
		cloudRange = options.cloudRange().get();
		cloudStatus = options.getCloudsType();
		graphicsStatus = options.graphicsPreset().get();
		ambientOcclusion = options.ambientOcclusion().get();
		particlesStatus = options.particles().get();
		entityShadows = options.entityShadows().get();
		entityDistance = options.entityDistanceScaling().get();
		cutoutLeaves = options.cutoutLeaves().get();
		improvedTransparency = options.improvedTransparency().get();
		weatherRadius = options.weatherRadius().get();
	}

	/*
	 * Apply or revert the graphics options for the specified graphics state.
	 */
	public static void applyOptions(Options options, GraphicsState state) {
		if (state == GraphicsState.DEFAULT) {
			options.biomeBlendRadius().set(biomeBlendRadius);
			options.cloudRange().set(cloudRange);
			options.cloudStatus().set(cloudStatus);
			options.graphicsPreset().set(graphicsStatus);
			options.ambientOcclusion().set(ambientOcclusion);
			options.particles().set(particlesStatus);
			options.entityShadows().set(entityShadows);
			options.entityDistanceScaling().set(entityDistance);
			options.cutoutLeaves().set(cutoutLeaves);
			options.improvedTransparency().set(improvedTransparency);
			options.weatherRadius().set(weatherRadius);
		} else { // state == GraphicsState.REDUCED
			options.cloudRange().set(0);
			options.cloudStatus().set(CloudStatus.OFF);
			options.particles().set(ParticleStatus.MINIMAL);
			options.entityShadows().set(false);
			options.entityDistanceScaling().set(0.5);
			options.weatherRadius().set(0);

			if (state == GraphicsState.MINIMAL) {
				options.biomeBlendRadius().set(0);
				options.ambientOcclusion().set(false);
				options.cutoutLeaves().set(false);
				options.improvedTransparency().set(false);
			}
		}
	}
}
