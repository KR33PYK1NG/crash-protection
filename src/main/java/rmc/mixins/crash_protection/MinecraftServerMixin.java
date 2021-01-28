package rmc.mixins.crash_protection;

import java.util.function.BooleanSupplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.server.MinecraftServer;

@Mixin(value = MinecraftServer.class)
public abstract class MinecraftServerMixin {

    private long lastReport;

    @Shadow
    protected abstract void tick(BooleanSupplier hasTimeLeft);

    @Redirect(method = "Lnet/minecraft/server/MinecraftServer;func_240802_v_()V",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/server/MinecraftServer;tick(Ljava/util/function/BooleanSupplier;)V"))
    private void redirectTick(MinecraftServer server, BooleanSupplier supplier) {
        try {
            this.tick(supplier);
        } catch (Exception ex) {
            long curReport = System.currentTimeMillis();
            if (curReport - lastReport > 1000) {
                System.out.println("Caught an exception that could cause a server crash!!!");
                ex.printStackTrace();
                lastReport = curReport;
            }
        }
    }

}