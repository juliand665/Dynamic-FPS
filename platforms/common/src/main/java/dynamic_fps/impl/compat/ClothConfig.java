package dynamic_fps.impl.compat;

import dynamic_fps.impl.Constants;
import dynamic_fps.impl.DynamicFPSMod;
import dynamic_fps.impl.config.BatteryTrackerConfig;
import dynamic_fps.impl.config.DynamicFPSConfig;
import dynamic_fps.impl.config.option.BatteryIndicatorPlacement;
import dynamic_fps.impl.config.option.BatteryIndicatorCondition;
import dynamic_fps.impl.config.option.GraphicsState;
import dynamic_fps.impl.PowerState;
import dynamic_fps.impl.config.Config;
import dynamic_fps.impl.config.option.IdleCondition;
import dynamic_fps.impl.util.Components;
import dynamic_fps.impl.config.option.IgnoreInitialClick;
import dynamic_fps.impl.util.VariableStepTransformer;
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

public final class ClothConfig {
	public static Screen genConfigScreen(Screen parent) {
		ConfigBuilder builder = ConfigBuilder.create()
			.setParentScreen(parent)
			.setTitle(Components.translatable("config", "title"))
			.setSavingRunnable(DynamicFPSMod::onConfigChanged);

		ConfigEntryBuilder entryBuilder = builder.entryBuilder();

		ConfigCategory general = builder.getOrCreateCategory(
			Components.translatable("config", "category.general")
		);

		DynamicFPSConfig config = DynamicFPSConfig.INSTANCE;
		DynamicFPSConfig defaultConfig = DynamicFPSConfig.DEFAULTS;

		SubCategoryBuilder misc = entryBuilder.startSubCategory(Components.translatable("config", "feature.misc"));

		misc.add(
			entryBuilder.startBooleanToggle(
				Components.translatable("config", "enabled"),
				config.enabled()
			)
			.setDefaultValue(defaultConfig.enabled())
			.setSaveConsumer(config::setEnabled)
			.build()
		);

		misc.add(
			entryBuilder.startBooleanToggle(
				Components.translatable("config", "uncap_menu_frame_rate"),
				config.uncapMenuFrameRate()
			)
			.setDefaultValue(defaultConfig.uncapMenuFrameRate())
			.setSaveConsumer(config::setUncapMenuFrameRate)
			.setTooltip(Components.translatable("config", "uncap_menu_frame_rate_tooltip"))
			.build()
		);

		misc.add(
			entryBuilder.startEnumSelector(
				Components.translatable("config", "ignore_initial_click"),
				IgnoreInitialClick.class,
				config.ignoreInitialClick()
			)
			.setDefaultValue(defaultConfig.ignoreInitialClick())
			.setSaveConsumer(config::setIgnoreInitialClick)
			.setEnumNameProvider(ClothConfig::ignoreInitialClickMessage)
			.setTooltip(Components.translatable("config", "ignore_initial_click_tooltip"))
			.build()
		);

		general.addEntry(misc.build());
		SubCategoryBuilder idle = entryBuilder.startSubCategory(Components.translatable("config", "feature.idle"));

		idle.add(
			entryBuilder.startIntSlider(
				Components.translatable("config", "idle_time"),
				config.idle().timeout() / 60,
				0, 30
			)
			.setDefaultValue(defaultConfig.idle().timeout() / 60)
			.setSaveConsumer(value -> config.idle().setTimeout(value * 60))
			.setTextGetter(ClothConfig::idleTimeMessage)
			.setTooltip(Components.translatable("config", "idle_time_tooltip"))
			.build()
		);

		idle.add(
			entryBuilder.startEnumSelector(
				Components.translatable("config", "idle_condition"),
				IdleCondition.class,
				config.idle().condition()
			)
			.setDefaultValue(defaultConfig.idle().condition())
			.setSaveConsumer(config.idle()::setCondition)
			.setEnumNameProvider(ClothConfig::IdleConditionMessage)
			.setTooltipSupplier(ClothConfig::idleConditionTooltip)
			.build()
		);

		general.addEntry(idle.build());
		SubCategoryBuilder volumeTransition = entryBuilder.startSubCategory(Components.translatable("config", "feature.volume_transition"));

		VariableStepTransformer volumeTransformer = getVolumeStepTransformer();

		volumeTransition.add(
			entryBuilder.startIntSlider(
				Components.translatable("config", "volume_transition_speed_up"),
				volumeTransformer.toStep((int) (config.volumeTransitionSpeed().getUp() * 100)),
				1, 73
			)
			.setDefaultValue(volumeTransformer.toStep((int) (defaultConfig.volumeTransitionSpeed().getUp() * 100)))
			.setSaveConsumer(step -> config.volumeTransitionSpeed().setUp((float) volumeTransformer.toValue(step) / 100))
			.setTextGetter(ClothConfig::volumeTransitionMessage)
			.setTooltip(Components.translatable("config", "volume_transition_speed_tooltip"))
			.build()
		);

		volumeTransition.add(
			entryBuilder.startIntSlider(
				Components.translatable("config", "volume_transition_speed_down"),
				volumeTransformer.toStep((int) (config.volumeTransitionSpeed().getDown() * 100)),
				1, 73
			)
			.setDefaultValue(volumeTransformer.toStep((int) (defaultConfig.volumeTransitionSpeed().getDown() * 100)))
			.setSaveConsumer(step -> config.volumeTransitionSpeed().setDown((float) volumeTransformer.toValue(step) / 100))
			.setTextGetter(ClothConfig::volumeTransitionMessage)
			.setTooltip(Components.translatable("config", "volume_transition_speed_tooltip"))
			.build()
		);

		general.addEntry(volumeTransition.build());
		SubCategoryBuilder battery = entryBuilder.startSubCategory(Components.translatable("config", "feature.battery"));

		BatteryTrackerConfig batteryTracker = config.batteryTracker();

		battery.add(
			entryBuilder.startBooleanToggle(
				Components.translatable("config", "battery_tracker"),
				batteryTracker.enabled()
			)
			.setDefaultValue(defaultConfig.batteryTracker().enabled())
			.setSaveConsumer(batteryTracker::setEnabled)
			.setTooltip(Components.translatable("config", "battery_tracker_tooltip"))
			.build()
		);

		battery.add(
			entryBuilder.startBooleanToggle(
				Components.translatable("config", "battery_tracker_switch_states"),
				batteryTracker.switchStates()
			)
			.setDefaultValue(defaultConfig.batteryTracker().switchStates())
			.setSaveConsumer(batteryTracker::setSwitchStates)
			.setTooltip(Components.translatable("config", "battery_tracker_switch_states_tooltip"))
			.build()
		);

		battery.add(
			entryBuilder.startBooleanToggle(
				Components.translatable("config", "battery_tracker_notifications"),
				batteryTracker.notifications()
			)
			.setDefaultValue(defaultConfig.batteryTracker().notifications())
			.setSaveConsumer(batteryTracker::setNotifications)
			.setTooltip(Components.translatable("config", "battery_tracker_notifications_tooltip"))
			.build()
		);

		battery.add(
			entryBuilder.startIntSlider(
				Components.translatable("config", "battery_critical_level"),
				batteryTracker.criticalLevel(),
				1,
				50
			)
			.setDefaultValue(defaultConfig.batteryTracker().criticalLevel())
			.setSaveConsumer(batteryTracker::setCriticalLevel)
			.setTextGetter(ClothConfig::valueAsPercentMessage)
			.setTooltip(Components.translatable("config", "battery_critical_level_tooltip"))
			.build()
		);

		battery.add(
			entryBuilder.startEnumSelector(
				Components.translatable("config", "battery_indicator_condition"),
				BatteryIndicatorCondition.class,
				batteryTracker.display().condition()
			)
			.setDefaultValue(defaultConfig.batteryTracker().display().condition())
			.setSaveConsumer(batteryTracker.display()::setCondition)
			.setEnumNameProvider(ClothConfig::batteryIndicatorConditionMessage)
			.build()
		);

		battery.add(
			entryBuilder.startEnumSelector(
				Components.translatable("config", "battery_indicator_placement"),
				BatteryIndicatorPlacement.class,
				batteryTracker.display().placement()
			)
			.setDefaultValue(defaultConfig.batteryTracker().display().placement())
			.setSaveConsumer(batteryTracker.display()::setPlacement)
			.setEnumNameProvider(ClothConfig::batteryIndicatorPlacementMessage)
			.build()
		);

		general.addEntry(battery.build());

		// Used for each state's frame rate target slider below
		VariableStepTransformer fpsTransformer = getFpsTransformer();

		for (PowerState state : PowerState.values()) {
			if (state.configurabilityLevel == PowerState.ConfigurabilityLevel.NONE) {
				continue;
			}

			Config instance = config.get(state);
			Config standard = defaultConfig.get(state);

			ConfigCategory category = builder.getOrCreateCategory(
				Components.translatable("config", "category." + state.toString().toLowerCase(Locale.ROOT))
			);

			// Having too many possible values on our slider is hard to use, so the conversion is not linear:
			// Instead the steps between possible values keep getting larger (from 1 to 2, 5, and finally 10)
			// Selecting the value all the way at the end sets no FPS limit, imitating the regular FPS slider
			category.addEntry(
				entryBuilder.startIntSlider(
					Components.translatable("config", "frame_rate_target"),
					fpsTransformer.toStep(instance.frameRateTarget()),
					0, 68
				)
				.setDefaultValue(fpsTransformer.toStep(standard.frameRateTarget()))
				.setSaveConsumer(step -> instance.setFrameRateTarget(fpsTransformer.toValue(step)))
				.setTextGetter(ClothConfig::fpsTargetMessage)
				.build()
			);

			category.addEntry(
				entryBuilder.startBooleanToggle(
					Components.translatable("options.vsync"),
					instance.enableVsync()
				)
				.setDefaultValue(standard.enableVsync())
				.setSaveConsumer(instance::setEnableVsync)
				.build()
			);

			// Further options are not allowed since this state is used while active.
			if (state.configurabilityLevel == PowerState.ConfigurabilityLevel.SOME) {
				continue;
			}

			SubCategoryBuilder volumes = entryBuilder.startSubCategory(Components.translatable("config", "volume_multiplier"));

			for (SoundSource source : SoundSource.values()) {
				String name = source.getName();

				volumes.add(
					entryBuilder.startIntSlider(
						Components.translatable("soundCategory." + name),
						(int) (instance.rawVolumeMultiplier(source) * 100),
						0, 100
					)
					.setDefaultValue((int) (standard.rawVolumeMultiplier(source) * 100))
					.setSaveConsumer(value -> instance.setVolumeMultiplier(source, value / 100f))
					.setTextGetter(ClothConfig::valueAsPercentMessage)
					.build()
				);
			}

			category.addEntry(volumes.build());

			category.addEntry(
				entryBuilder.startEnumSelector(
					Components.translatable("config", "graphics_state"),
					GraphicsState.class,
					instance.graphicsState()
				)
				.setDefaultValue(standard.graphicsState())
				.setSaveConsumer(instance::setGraphicsState)
				.setEnumNameProvider(ClothConfig::graphicsStateMessage)
				.setTooltipSupplier(ClothConfig::graphicsStateTooltip)
				.build()
			);

			category.addEntry(
				entryBuilder.startBooleanToggle(
					Components.translatable("config", "show_toasts"),
					instance.showToasts()
				)
				.setDefaultValue(standard.showToasts())
				.setSaveConsumer(instance::setShowToasts)
				.setTooltip(Components.translatable("config", "show_toasts_tooltip"))
				.build()
			);

			category.addEntry(
				entryBuilder.startBooleanToggle(
					Components.translatable("config", "run_garbage_collector"),
					instance.runGarbageCollector()
				)
				.setDefaultValue(standard.runGarbageCollector())
				.setSaveConsumer(instance::setRunGarbageCollector)
				.setTooltip(Components.translatable("config", "run_garbage_collector_tooltip"))
				.build()
			);
		}

		ConfigCategory advanced = builder.getOrCreateCategory(
			Components.translatable("config", "category.advanced")
		);

		advanced.addEntry(
			entryBuilder.startBooleanToggle(
				Components.translatable("config", "download_natives"),
				config.downloadNatives()
			)
			.setDefaultValue(defaultConfig.downloadNatives())
			.setSaveConsumer(config::setDownloadNatives)
			.setTooltip(new Component[]{
				Components.translatable("config", "download_natives_description_0"),
				Components.translatable("config", "download_natives_description_1")}
			)
			.build()
		);

		advanced.addEntry(
			entryBuilder.startBooleanToggle(
				Components.translatable("config", "mock_battery_data"),
				config.mockBatteryData()
			)
			.setDefaultValue(defaultConfig.mockBatteryData())
			.setSaveConsumer(config::setMockBatteryData)
			.build()
		);

		return builder.build();
	}

	private static Component idleTimeMessage(int value) {
		if (value == 0) {
			return Components.translatable("config", "disabled");
		} else {
			return Components.translatable("config", "minutes", value);
		}
	}

	private static VariableStepTransformer getVolumeStepTransformer() {
		VariableStepTransformer transformer = new VariableStepTransformer();

		// Since the transformer only works with integers
		// We multiply the percentage by 100 to work with it
		transformer.addStep(1, 50);
		transformer.addStep(5, 100);
		transformer.addStep(10, 200);
		transformer.addStep(100, 300);
		transformer.addStep(700, 1000);

		return transformer;
	}

	private static Component volumeTransitionMessage(int step) {
		int value = getVolumeStepTransformer().toValue(step);

		if (value <= 300) {
			return Components.literal(value + "%");
		} else {
			return Components.translatable("config", "volume_transition_speed_instant");
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
			return Components.translatable("options.framerate", fps);
		} else {
			return Components.translatable("options.framerateLimit.max");
		}
	}

	private static Component valueAsPercentMessage(int value) {
		return Components.literal(Integer.toString(value) + "%");
	}

	public static Component ignoreInitialClickMessage(Enum<IdleCondition> state) {
		return Components.translatable("config", "ignore_initial_click_" + state.toString().toLowerCase(Locale.ROOT));
	}

	public static Component IdleConditionMessage(Enum<IdleCondition> state) {
		return Components.translatable("config", "idle_condition_" + state.toString().toLowerCase(Locale.ROOT));
	}

	private static Optional<Component[]> idleConditionTooltip(IdleCondition condition) {
		return Optional.of(new Component[]{ Components.translatable("config", "idle_condition_" + condition.toString().toLowerCase(Locale.ROOT) + "_tooltip") });
	}

	private static Component graphicsStateMessage(Enum<GraphicsState> graphicsState) {
		return Components.translatable("config", "graphics_state_" + graphicsState.toString().toLowerCase(Locale.ROOT));
	}

	public static Component batteryIndicatorConditionMessage(Enum<BatteryIndicatorCondition> state) {
		return Components.translatable("config", "battery_indicator_condition_" + state.toString().toLowerCase(Locale.ROOT));
	}

	public static Component batteryIndicatorPlacementMessage(Enum<BatteryIndicatorPlacement> state) {
		return Components.translatable("config", "battery_indicator_placement_" + state.toString().toLowerCase(Locale.ROOT));
	}

	private static Optional<Component[]> graphicsStateTooltip(GraphicsState graphicsState) {
		if (!graphicsState.equals(GraphicsState.MINIMAL)) {
			return Optional.empty();
		}

		return Optional.of(new Component[]{ Components.translatable("config", "graphics_state_minimal_tooltip").withStyle(ChatFormatting.RED) });
	}
}
