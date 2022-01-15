package dynamicfps;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.IOException;

public final class DynamicFPSConfig {
	private transient File file;
	/// Whether to disable or enable the frame rate drop when unfocused.
	public boolean reduceFPSWhenUnfocused = true;
	/// The frame rate to target when unfocused (only applies if `enableUnfocusedFPS` is true).
	public int unfocusedFPS = 1;
	/// Whether to uncap FPS when hovered, even if it would otherwise be reduced.
	public boolean restoreFPSWhenHovered = true;
	/// Volume multiplier when not focused.
	public float unfocusedVolumeMultiplier = 0.25f;
	/// Volume multiplier when not visible.
	public float hiddenVolumeMultiplier = 0f;
	/// Whether to trigger a garbage collector run whenever the game is unfocused.
	public boolean runGCOnUnfocus = false;
	
	private DynamicFPSConfig() {}
	
	public static DynamicFPSConfig load() {
		File file = new File(
			FabricLoader.getInstance().getConfigDir().toString(),
			DynamicFPSMod.MOD_ID + ".toml"
		);
		
		DynamicFPSConfig config;
		if (file.exists()) {
			Toml configTOML = new Toml().read(file);
			config = configTOML.to(DynamicFPSConfig.class);
			config.file = file;
		} else {
			config = new DynamicFPSConfig();
			config.file = file;
			config.save();
		}
		return config;
	}
	
	public void save() {
		TomlWriter writer = new TomlWriter();
		try {
			writer.write(this, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
