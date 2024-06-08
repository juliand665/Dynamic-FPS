package net.lostluma.dynamic_fps.impl.forge.compat;

import net.minecraftforge.fml.IExtensionPoint;

import java.util.function.Supplier;

public class FrexExtension {
	public record Factory(Supplier<Boolean> api) implements IExtensionPoint<Factory> {}
}
