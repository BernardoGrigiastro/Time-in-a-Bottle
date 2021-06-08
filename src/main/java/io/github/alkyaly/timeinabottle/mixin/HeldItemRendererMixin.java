package io.github.alkyaly.timeinabottle.mixin;

import io.github.alkyaly.timeinabottle.TimeInABottle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {

    @Shadow private ItemStack mainHand;

    @Shadow @Final private MinecraftClient client;

    //Makes the Time in a Bottle not bob when updating NBT
    @Inject(at = @At("HEAD"), method = "updateHeldItems")
    private void updateHeldItems(CallbackInfo ci) {
        ItemStack stack = this.client.player.getMainHandStack();
        if (this.mainHand.getItem() == TimeInABottle.TIME_IN_A_BOTTLE && stack.getItem() == TimeInABottle.TIME_IN_A_BOTTLE) {
            mainHand = stack;
        }
    }
}
