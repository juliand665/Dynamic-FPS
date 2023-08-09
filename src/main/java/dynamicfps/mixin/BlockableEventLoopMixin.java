package dynamicfps.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.util.thread.BlockableEventLoop;

import java.util.concurrent.locks.LockSupport;

@Mixin(BlockableEventLoop.class)
public final class BlockableEventLoopMixin {
	/**
	 @author Julian Dunskus
	 @reason The vanilla version is simply broken, taking up way too many resources in the background.
	 */
	@Overwrite
	public void waitForTasks() {
		// yield() here is a terrible idea
		LockSupport.parkNanos("waiting for tasks", 500_000); // increased wait to 0.5 ms
	}
}
