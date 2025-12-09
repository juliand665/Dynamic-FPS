package dynamic_fps.impl.util;

import net.minecraft.resources.Identifier;

public class ResourceLocations {
	public static Identifier of(String namespace, String path) {
		return Identifier.fromNamespaceAndPath(namespace, path);
	}
}
