package dynamic_fps.impl.util;

import net.minecraft.resources.ResourceLocation;

public class ResourceLocations {
	public static ResourceLocation of(String namespace, String path) {
		return ResourceLocation.fromNamespaceAndPath(namespace, path);
	}
}
