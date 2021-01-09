package dynamicfps;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;

public class DynamicFPSModMenu implements ModMenuApi {
	protected static final float RESOLUTION = 1000.0F;


	@Override
	@Deprecated
	public String getModId() {
		return DynamicFPSMod.MOD_ID;
	}

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return DynamicFPSModMenu::genConfig;
	}

	private static Screen genConfig(Screen parent) {
		ConfigBuilder builder = ConfigBuilder.create()
			.setParentScreen(parent)
			.setTitle(new TranslatableText("title.dynamicfps.config"));
		ConfigEntryBuilder eBuilder = builder.entryBuilder();
		ConfigCategory general = builder.getOrCreateCategory(new TranslatableText("category.dynamicfps.general"));
		general.addEntry(
			eBuilder.startBooleanToggle(new TranslatableText("button.dynamicfps.enableunfocused"), DynamicFPSMod.config.enableUnfocusedFps)
			.setSaveConsumer(nVal -> DynamicFPSMod.config.enableUnfocusedFps = nVal)
			.build());
		general.addEntry(
			eBuilder.startIntSlider(new TranslatableText("button.dynamicfps.unfocusedtarget"), DynamicFPSMod.config.getFramerateTarget(), 1, 60)
			.setSaveConsumer(nVal -> DynamicFPSMod.config.setFramerateTarget(nVal))
			.build());

		builder.setSavingRunnable(() -> DynamicFPSMod.config.saveConfig());
		return builder.build();
	}
}