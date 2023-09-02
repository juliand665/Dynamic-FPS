package dynamic_fps.impl.compat;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;

import static dynamic_fps.impl.util.Localization.localized;

import dynamic_fps.impl.DynamicFPSMod;
import dynamic_fps.impl.GraphicsState;
import dynamic_fps.impl.PowerState;
import dynamic_fps.impl.config.DynamicFPSConfig;

public final class ClothConfig {
	public static Screen genConfigScreen(Screen parent) {
		ConfigBuilder builder = ConfigBuilder.create()
			.setParentScreen(parent)
			.setTitle(localized("config", "title"))
			.setSavingRunnable(DynamicFPSMod.modConfig::save);

		ConfigEntryBuilder entryBuilder = builder.entryBuilder();

		for (var state : PowerState.values()) {
			if (!state.configurable) {
				continue;
			}

			var config = DynamicFPSMod.modConfig.get(state);
			var standard = DynamicFPSConfig.getDefaultConfig(state);

			builder.getOrCreateCategory(
				localized("config", "category." + state.toString().toLowerCase()))
				.addEntry(
					entryBuilder
						.startTextDescription(
							localized("config", "frame_rate_target_description")).build())
				.addEntry(entryBuilder
					.startIntSlider(
						localized("config", "frame_rate_target"),
						config.frameRateTarget(),
						-1, 60)
					.setDefaultValue(standard.frameRateTarget())
					.setSaveConsumer(config::setFrameRateTarget)
					.build())
				.addEntry(entryBuilder
					.startIntSlider(
						localized("config", "volume_multiplier"),
						(int) (config.volumeMultiplier() * 100),
						0, 100)
					.setDefaultValue((int) (standard.volumeMultiplier() * 100))
					.setSaveConsumer(value -> config.setVolumeMultiplier(value / 100f))
					.build())
				.addEntry(entryBuilder
					.startEnumSelector(
						localized("config", "graphics_state"),
						GraphicsState.class,
						config.graphicsState())
					.setDefaultValue(standard.graphicsState())
					.setSaveConsumer(config::setGraphicsState)
					.build())
				.addEntry(entryBuilder
					.startBooleanToggle(
						localized("config", "show_toasts"),
						config.showToasts())
					.setDefaultValue(standard.showToasts())
					.setSaveConsumer(config::setShowToasts)
					.build())
				.addEntry(entryBuilder
					.startBooleanToggle(
						localized("config", "run_garbage_collector"),
						config.runGarbageCollector())
					.setDefaultValue(standard.runGarbageCollector())
					.setSaveConsumer(config::setRunGarbageCollector)
					.build());
		}

		return builder.build();
	}
}
