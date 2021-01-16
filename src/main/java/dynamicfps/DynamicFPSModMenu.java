package dynamicfps;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;

import static dynamicfps.util.Localization.localized;

public class DynamicFPSModMenu implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return DynamicFPSModMenu::genConfig;
	}
	
	private static Screen genConfig(Screen parent) {
		ConfigBuilder builder = ConfigBuilder.create()
			.setParentScreen(parent)
			.setTitle(localized("title", "config"))
			.setSavingRunnable(DynamicFPSMod.config::save);
		ConfigEntryBuilder entryBuilder = builder.entryBuilder();
		
		// general
		builder.getOrCreateCategory(localized("category", "general"))
			.addEntry(entryBuilder
				.startBooleanToggle(
					localized("button", "reduce_when_unfocused"),
					DynamicFPSMod.config.reduceFPSWhenUnfocused
				)
				.setSaveConsumer(value -> DynamicFPSMod.config.reduceFPSWhenUnfocused = value)
				.build()
			)
			.addEntry(entryBuilder
				.startIntSlider(
					localized("button", "unfocused_fps"),
					DynamicFPSMod.config.unfocusedFPS,
					0, 60
				)
				.setSaveConsumer(value -> DynamicFPSMod.config.unfocusedFPS = value)
				.build()
			)
			.addEntry(entryBuilder
				.startBooleanToggle(
					localized("button", "restore_when_hovered"),
					DynamicFPSMod.config.restoreFPSWhenHovered
				)
				.setSaveConsumer(value -> DynamicFPSMod.config.restoreFPSWhenHovered = value)
				.build()
			);
		
		return builder.build();
	}
}