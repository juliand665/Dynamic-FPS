package dynamic_fps.impl.mixin.bugfix;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.util.thread.BlockableEventLoop;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * @author Julian Dunskus
 * @reason The vanilla version of `waitForTasks` is simply broken, taking up way too many resources in the background.
 */
@Mixin(BlockableEventLoop.class)
public final class BlockableEventLoopMixin {
	/*
	Replaced with other injections due to NeoForge build issue.

	@Overwrite
	public void waitForTasks() {
		// yield() here is a terrible idea
		LockSupport.parkNanos("waiting for tasks", 500_000); // increased wait to 0.5 ms
	}
	 */

	/**
	 * Skip the Thread.yield() call.
	 */
	@WrapWithCondition(method = "waitForTasks", at = @At(value = "INVOKE", target = "Ljava/lang/Thread;yield()V"))
	private boolean skipYield() {
		return false;
	}

	/**
	 * Increase wait time to 0.5 milliseconds.
	 */
	@ModifyConstant(method = "waitForTasks", constant = @Constant(longValue = 100000L))
	private long parkNanosTime(long original) {
		return 500_000L;
	}
}
