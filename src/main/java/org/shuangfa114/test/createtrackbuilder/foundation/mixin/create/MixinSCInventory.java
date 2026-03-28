package org.shuangfa114.test.createtrackbuilder.foundation.mixin.create;

import com.simibubi.create.content.schematics.cannon.SchematicannonBlockEntity;
import com.simibubi.create.content.schematics.cannon.SchematicannonInventory;
import net.minecraft.world.item.ItemStack;
import org.shuangfa114.test.createtrackbuilder.api.TrackPrinter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SchematicannonInventory.class,remap = false)
public class MixinSCInventory {
    @Shadow
    @Final
    private SchematicannonBlockEntity blockEntity;

    @Inject(method = "isItemValid", at = @At("HEAD"), cancellable = true)
    private void isItemValid(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (blockEntity.printer instanceof TrackPrinter) {
            cir.setReturnValue(false);
        }
    }
}
