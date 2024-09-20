package net.lostluma.dynamic_fps.impl.forge.service;

import com.electronwill.nightconfig.core.CommentedConfig;
import dynamic_fps.impl.service.ModCompat;
import dynamic_fps.impl.service.Platform;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;

import java.util.HashSet;
import java.util.Set;

public class ForgeModCompat implements ModCompat {
	private static boolean disableOverlayOptimization = false;
	private static final Set<String> optedOutScreens = new HashSet<>();

	static {
		optedOutScreens.add(ReceivingLevelScreen.class.getCanonicalName());
		ModList.get().getMods().forEach(ForgeModCompat::parseModMetadata);
	}

	@Override
	public boolean isDisabled() {
		return false;
	}

	@Override
	public boolean disableOverlayOptimization() {
		return disableOverlayOptimization || Platform.getInstance().isModLoaded("rrls");
	}

	@Override
	public Set<String> getOptedInScreens() {
		return Set.of();
	}

	@Override
	public Set<String> getOptedOutScreens() {
		return optedOutScreens;
	}

	private static void parseModMetadata(IModInfo modInfo) {
		Object config = modInfo.getModProperties().get("dynamic_fps");

		if (config == null) {
			return;
		}

		Boolean value = ((CommentedConfig) config).get("optimized_overlay");

		if (value != null && !value) {
			disableOverlayOptimization = true;
		}
	}
}
