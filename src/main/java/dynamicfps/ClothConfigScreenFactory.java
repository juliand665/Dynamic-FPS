package dynamicfps;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;

import static dynamicfps.util.Localization.localized;

public final class ClothConfigScreenFactory {
	static Screen genConfig(Screen parent) {
		ConfigBuilder builder = ConfigBuilder.create()
			.setParentScreen(parent)
			.setTitle(localized("config", "title"))
			.setSavingRunnable(DynamicFPSMod.config::save);
		ConfigEntryBuilder entryBuilder = builder.entryBuilder();
		
		// general
		builder.getOrCreateCategory(localized("config", "category.general"))
			.addEntry(entryBuilder
				.startBooleanToggle(
					localized("config", "reduce_when_unfocused"),
					DynamicFPSMod.config.reduceFPSWhenUnfocused
				)
				.setSaveConsumer(value -> DynamicFPSMod.config.reduceFPSWhenUnfocused = value)
				.build()
			)
			.addEntry(entryBuilder
				.startIntSlider(
					localized("config", "unfocused_fps"),
					DynamicFPSMod.config.unfocusedFPS,
					0, 60
				)
				.setSaveConsumer(value -> DynamicFPSMod.config.unfocusedFPS = value)
				.build()
			)
			.addEntry(entryBuilder
				.startBooleanToggle(
					localized("config", "restore_when_hovered"),
					DynamicFPSMod.config.restoreFPSWhenHovered
				)
				.setSaveConsumer(value -> DynamicFPSMod.config.restoreFPSWhenHovered = value)
				.build()
			)
			.addEntry(entryBuilder
				.startIntSlider(
					localized("config", "unfocused_volume"),
					(int) (DynamicFPSMod.config.unfocusedVolumeMultiplier * 100),
					0, 100
				)
				.setSaveConsumer(value -> DynamicFPSMod.config.unfocusedVolumeMultiplier = value / 100f)
				.build()
			)
			.addEntry(entryBuilder
				.startIntSlider(
					localized("config", "hidden_volume"),
					(int) (DynamicFPSMod.config.hiddenVolumeMultiplier * 100),
					0, 100
				)
				.setSaveConsumer(value -> DynamicFPSMod.config.hiddenVolumeMultiplier = value / 100f)
				.build()
			)
			.addEntry(entryBuilder
				.startBooleanToggle(
					localized("config", "run_gc_on_unfocus"),
					DynamicFPSMod.config.runGCOnUnfocus
				)
				.setSaveConsumer(value -> DynamicFPSMod.config.runGCOnUnfocus = value)
				.build()
			);
		
		return builder.build();
	}
}
