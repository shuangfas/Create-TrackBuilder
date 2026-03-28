package org.shuangfa114.test.createtrackbuilder.foundation.mixin.create;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.schematics.SchematicPrinter;
import com.simibubi.create.content.schematics.cannon.SchematicannonBlockEntity;
import com.simibubi.create.content.schematics.cannon.SchematicannonInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.shuangfa114.test.createtrackbuilder.api.TrackPrinter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = SchematicannonBlockEntity.class, remap = false)
public abstract class MixinSchematicannonBE {
    @Shadow
    public SchematicannonInventory inventory;
    @Shadow
    public String statusMsg;
    @Shadow
    public SchematicannonBlockEntity.State state;
    @Shadow
    public boolean sendUpdate;
    @Shadow
    public SchematicPrinter printer;
    @Shadow
    public int blocksToPlace;
    @Shadow
    public int blocksPlaced;

    @Shadow
    public abstract void updateChecklist();

    @WrapOperation(method = "tickPrinter", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/schematics/cannon/SchematicannonBlockEntity;initializePrinter(Lnet/minecraft/world/item/ItemStack;)V"))
    private void initializePrinter(SchematicannonBlockEntity instance, ItemStack blueprint, Operation<Void> original) {
        Level level = instance.getLevel();
        if (!isTrackPrinter()) {
            original.call(instance, blueprint);
            return;
        }
        printer.loadSchematic(blueprint, level, false);
        if (printer.isErrored()) {
            state = SchematicannonBlockEntity.State.STOPPED;
            statusMsg = "schematicErrored";
            inventory.setStackInSlot(0, ItemStack.EMPTY);
            inventory.setStackInSlot(1, new ItemStack(AllItems.EMPTY_SCHEMATIC.get()));
            printer.resetSchematic();
            sendUpdate = true;
            return;
        }
        if (printer.isWorldEmpty()) {
            state = SchematicannonBlockEntity.State.STOPPED;
            statusMsg = "schematicExpired";
            inventory.setStackInSlot(0, ItemStack.EMPTY);
            inventory.setStackInSlot(1, new ItemStack(AllItems.EMPTY_SCHEMATIC.get()));
            printer.resetSchematic();
            sendUpdate = true;
            return;
        }
        state = SchematicannonBlockEntity.State.PAUSED;
        statusMsg = "ready";
        updateChecklist();
        sendUpdate = true;
        blocksToPlace += blocksPlaced;
    }

    @WrapOperation(method = "tickPrinter", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z", ordinal = 0))
    private boolean isEmpty(ItemStack instance, Operation<Boolean> original) {
        return !isTrackPrinter() && original.call(instance);
    }

    @WrapOperation(method = "finishedPrinting", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/schematics/cannon/SchematicannonBlockEntity;resetPrinter()V"))
    private void resetToNormalPrinter(SchematicannonBlockEntity instance, Operation<Void> original) {
        printer = new SchematicPrinter();
        original.call(instance);
    }

    @WrapOperation(method = "finishedPrinting", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/schematics/cannon/SchematicannonInventory;setStackInSlot(ILnet/minecraft/world/item/ItemStack;)V"))
    private void notOutputWhenControlled(SchematicannonInventory instance, int i, ItemStack stack, Operation<Void> original) {
        if (!isTrackPrinter()) {
            original.call(instance, i, stack);
        }
    }

    @Unique
    public boolean isTrackPrinter() {
        return printer instanceof TrackPrinter;
    }
}
