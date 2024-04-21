package dynamic_fps.impl.compat;

import dynamic_fps.impl.DynamicFPSMod;
import dynamic_fps.impl.GraphicsState;
import dynamic_fps.impl.PowerState;
import dynamic_fps.impl.config.Config;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;

import java.util.Locale;
import java.util.Optional;

import static dynamic_fps.impl.util.Localization.localized;

public final class ClothConfig {
	public static Screen genConfigScreen(Screen parent) {
		ConfigBuilder builder = ConfigBuilder.create()
			.setParentScreen(parent)
			.setTitle(localized("config", "title"))
			.setSavingRunnable(DynamicFPSMod::onConfigChanged);

		ConfigEntryBuilder entryBuilder = builder.entryBuilder();

		ConfigCategory general = builder.getOrCreateCategory(
			localized("config", "category.general")
		);

		general.addEntry(
			entryBuilder.startBooleanToggle(
				localized("config", "enabled"),
				DynamicFPSMod.modConfig.enabled()
			)
			.setDefaultValue(true)
			.setSaveConsumer(DynamicFPSMod.modConfig::setEnabled)
			.build()
		);

		general.addEntry(
			entryBuilder.startTextDescription(Component.literal(" ")).build()
		);

		general.addEntry(
			entryBuilder.startIntSlider(
				localized("config", "idle_time"),
				DynamicFPSMod.modConfig.idleTime() / 60,
				0, 30
			)
			.setDefaultValue(0)
			.setSaveConsumer(value -> DynamicFPSMod.modConfig.setIdleTime(value * 60))
			.setTextGetter(ClothConfig::idleTimeMessage)
			.setTooltip(localized("config", "idle_time_tooltip"))
			.build()
		);

		general.addEntry(
			entryBuilder.startBooleanToggle(
				localized("config", "uncap_menu_frame_rate"),
				DynamicFPSMod.modConfig.uncapMenuFrameRate()
			)
			.setDefaultValue(false)
			.setSaveConsumer(DynamicFPSMod.modConfig::setUncapMenuFrameRate)
			.setTooltip(localized("config", "uncap_menu_frame_rate_tooltip"))
			.build()
		);

		for (PowerState state : PowerState.values()) {
			if (!state.configurable) {
				continue;
			}

			Config config = DynamicFPSMod.modConfig.get(state);
			Config standard = Config.getDefault(state);

			ConfigCategory category = builder.getOrCreateCategory(
				localized("config", "category." + state.toString().toLowerCase())
			);

			category.addEntry(
				entryBuilder.startIntSlider(
					localized("config", "frame_rate_target"),
					fromConfigFpsTarget(config.frameRateTarget()),
					0, 61
				)
				.setDefaultValue(fromConfigFpsTarget(standard.frameRateTarget()))
				.setSaveConsumer(value -> config.setFrameRateTarget(toConfigFpsTarget(value)))
				.setTextGetter(ClothConfig::fpsTargetMessage)
				.build()
			);

			SubCategoryBuilder volumes = entryBuilder.startSubCategory(localized("config", "volume_multiplier"));

			for (SoundSource source : SoundSource.values()) {
				String name = source.getName();

				volumes.add(
					entryBuilder.startIntSlider(
						Component.translatable("soundCategory." + name),
						(int) (config.rawVolumeMultiplier(source) * 100),
						0, 100
					)
					.setDefaultValue((int) (standard.rawVolumeMultiplier(source) * 100))
					.setSaveConsumer(value -> config.setVolumeMultiplier(source, value / 100f))
					.setTextGetter(ClothConfig::volumeMultiplierMessage)
					.build()
				);
			}

			category.addEntry(volumes.build());

			category.addEntry(
				entryBuilder.startEnumSelector(
					localized("config", "graphics_state"),
					GraphicsState.class,
					config.graphicsState()
				)
				.setDefaultValue(standard.graphicsState())
				.setSaveConsumer(config::setGraphicsState)
				.setEnumNameProvider(ClothConfig::graphicsStateMessage)
				.setTooltipSupplier(ClothConfig::graphicsStateTooltip)
				.build()
			);

			category.addEntry(
				entryBuilder.startBooleanToggle(
					localized("config", "show_toasts"),
					config.showToasts()
				)
				.setDefaultValue(standard.showToasts())
				.setSaveConsumer(config::setShowToasts)
				.setTooltip(localized("config", "show_toasts_tooltip"))
				.build()
			);

			category.addEntry(
				entryBuilder.startBooleanToggle(
					localized("config", "run_garbage_collector"),
					config.runGarbageCollector()
				)
				.setDefaultValue(standard.runGarbageCollector())
				.setSaveConsumer(config::setRunGarbageCollector)
				.setTooltip(localized("config", "run_garbage_collector_tooltip"))
				.build()
			);
		}

		return builder.build();
	}

	private static Component idleTimeMessage(int value) {
		if (value == 0) {
			return localized("config", "disabled");
		} else {
			return localized("config", "minutes", value);
		}
	}

	// Convert magic -1 number to 61 (and reverse)
	// So the "unlocked" FPS value is on the right
	private static int toConfigFpsTarget(int value) {
		return value == 61 ? -1 : value;
	}

	private static int fromConfigFpsTarget(int value) {
		return value == -1 ? 61 : value;
	}

	private static Component fpsTargetMessage(int value) {
		if (toConfigFpsTarget(value) != -1) {
			return Component.translatable("options.framerate", value);
		} else {
			return Component.translatable("options.framerateLimit.max");
		}
	}

	private static Component volumeMultiplierMessage(int value) {
		return Component.literal(Integer.toString(value) + "%");
	}

	private static Component graphicsStateMessage(Enum<GraphicsState> graphicsState) {
		return localized("config", "graphics_state_" + graphicsState.toString().toLowerCase(Locale.ROOT));
	}

	private static Optional<Component[]> graphicsStateTooltip(GraphicsState graphicsState) {
		if (!graphicsState.equals(GraphicsState.MINIMAL)) {
			return Optional.empty();
		}

		return Optional.of(new Component[]{ localized("config", "graphics_state_minimal_tooltip").withStyle(ChatFormatting.RED) });
	}
}
