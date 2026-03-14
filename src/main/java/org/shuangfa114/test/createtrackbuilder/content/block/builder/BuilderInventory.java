package org.shuangfa114.test.createtrackbuilder.content.block.builder;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.shuangfa114.test.createtrackbuilder.content.item.editor.TrackEditor;

public class BuilderInventory extends ItemStackHandler {
    public BuilderBlockEntity blockEntity;

    public BuilderInventory(BuilderBlockEntity blockEntity) {
        super(1);
        this.blockEntity = blockEntity;
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        blockEntity.setChanged();
    }


    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return stack.getItem() instanceof TrackEditor;
    }
}
