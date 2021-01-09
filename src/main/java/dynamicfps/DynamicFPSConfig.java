package dynamicfps;

import java.io.File;
import java.io.IOException;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;

import net.fabricmc.loader.api.FabricLoader;

public class DynamicFPSConfig {
	
	private transient File file;
	/// Toggle for whether to disable or enable the framerate drop when unfocused
	public boolean enableUnfocusedFps = true;
	/// The framerate to target when unfocused (only applies if `enableUnfocusedFps` is true)
	private int framerateTarget = 1;
	public transient int millisecondsTarget = 1000;

	private DynamicFPSConfig() {}

	public static DynamicFPSConfig getConfig() {
		File file = new File(FabricLoader.getInstance().getConfigDir().toString(), DynamicFPSMod.MOD_ID + ".toml");
		if (file.exists()) {
			Toml configToml = new Toml().read(file);
			DynamicFPSConfig config = configToml.to(DynamicFPSConfig.class);
			config.file = file;
			config.millisecondsTarget = 1000 / config.framerateTarget;
			return config;
		} else {
			DynamicFPSConfig config = new DynamicFPSConfig();
			config.file = file;
			config.saveConfig();
			return config;
		}
	}
	
	public int getFramerateTarget() {
		return framerateTarget;
	}

	public void setFramerateTarget(int target) {
		framerateTarget = target;
		millisecondsTarget = 1000 / framerateTarget;
	}

	public void saveConfig() {
		TomlWriter tWr = new TomlWriter();
		try {
			tWr.write(this, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
