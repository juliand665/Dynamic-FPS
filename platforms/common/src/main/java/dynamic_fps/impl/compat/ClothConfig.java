package dynamic_fps.impl.compat;

import dynamic_fps.impl.Constants;
import dynamic_fps.impl.DynamicFPSMod;
import dynamic_fps.impl.config.option.GraphicsState;
import dynamic_fps.impl.PowerState;
import dynamic_fps.impl.config.Config;
import dynamic_fps.impl.util.VariableStepTransformer;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
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
			entryBuilder.startTextDescription(CommonComponents.SPACE).build()
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

		VariableStepTransformer volumeTransformer = getVolumeStepTransformer();

		general.addEntry(
			entryBuilder.startIntSlider(
					localized("config", "volume_transition_speed_up"),
					volumeTransformer.toStep((int) (DynamicFPSMod.volumeTransitionSpeed().getUp() * 10)),
					1, 31
				)
				.setDefaultValue(volumeTransformer.toStep((int) (1.0f * 10)))
				.setSaveConsumer(step -> DynamicFPSMod.volumeTransitionSpeed().setUp((float) volumeTransformer.toValue(step) / 10))
				.setTextGetter(ClothConfig::volumeTransitionMessage)
				.setTooltip(localized("config", "volume_transition_speed_tooltip"))
				.build()
		);

		general.addEntry(
			entryBuilder.startIntSlider(
					localized("config", "volume_transition_speed_down"),
					volumeTransformer.toStep((int) (DynamicFPSMod.volumeTransitionSpeed().getDown() * 10)),
					1, 31
				)
				.setDefaultValue(volumeTransformer.toStep((int) (0.5f * 10)))
				.setSaveConsumer(step -> DynamicFPSMod.volumeTransitionSpeed().setDown((float) volumeTransformer.toValue(step) / 10))
				.setTextGetter(ClothConfig::volumeTransitionMessage)
				.setTooltip(localized("config", "volume_transition_speed_tooltip"))
				.build()
		);

		// Used for each state's frame rate target slider below
		VariableStepTransformer fpsTransformer = getFpsTransformer();

		for (PowerState state : PowerState.values()) {
			if (!state.configurable) {
				continue;
			}

			Config config = DynamicFPSMod.modConfig.get(state);
			Config standard = Config.getDefault(state);

			ConfigCategory category = builder.getOrCreateCategory(
				localized("config", "category." + state.toString().toLowerCase())
			);

			// Having too many possible values on our slider is hard to use, so the conversion is not linear:
			// Instead the steps between possible values keep getting larger (from 1 to 2, 5, and finally 10)
			// Selecting the value all the way at the end sets no FPS limit, imitating the regular FPS slider
			category.addEntry(
				entryBuilder.startIntSlider(
					localized("config", "frame_rate_target"),
					fpsTransformer.toStep(config.frameRateTarget()),
					0, 68
				)
				.setDefaultValue(fpsTransformer.toStep(standard.frameRateTarget()))
				.setSaveConsumer(step -> config.setFrameRateTarget(fpsTransformer.toValue(step)))
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

	private static VariableStepTransformer getVolumeStepTransformer() {
		VariableStepTransformer transformer = new VariableStepTransformer();

		transformer.addStep(1, 30);
		transformer.addStep(970, 1000);

		return transformer;
	}

	private static Component volumeTransitionMessage(int step) {
		float value = (float) getVolumeStepTransformer().toValue(step) / 10;

		if (value < 100.0f) {
			return Component.literal(value + "%");
		} else {
			return localized("config", "volume_transition_speed_instant");
		}
	}

	private static VariableStepTransformer getFpsTransformer() {
		VariableStepTransformer transformer = new VariableStepTransformer();

		transformer.addStep(1, 20);
		transformer.addStep(2, 72);
		transformer.addStep(3, 75);
		transformer.addStep(5, 100);
		transformer.addStep(10, 260);

		return transformer;
	}

	private static Component fpsTargetMessage(int step) {
		int fps = getFpsTransformer().toValue(step);

		if (fps != Constants.NO_FRAME_RATE_LIMIT) {
			return Component.translatable("options.framerate", fps);
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
