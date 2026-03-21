package org.shuangfa114.test.createtrackbuilder.content.block.builder;

import com.simibubi.create.foundation.gui.menu.MenuBase;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;
import org.shuangfa114.test.createtrackbuilder.ModMenuTypes;

public class BuilderMenu extends MenuBase<BuilderBlockEntity> {
    public BuilderMenu(MenuType<?> type, int id, Inventory inv, FriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    public BuilderMenu(MenuType<?> type, int id, Inventory inv, BuilderBlockEntity contentHolder) {
        super(type, id, inv, contentHolder);
    }

    public static BuilderMenu create(int id, Inventory inv, BuilderBlockEntity contentHolder) {
        return new BuilderMenu(ModMenuTypes.BUILDER.get(), id, inv, contentHolder);
    }

    @Override
    protected BuilderBlockEntity createOnClient(FriendlyByteBuf extraData) {
        BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(extraData.readBlockPos());
        if (blockEntity instanceof BuilderBlockEntity builderBlockEntity) {
            return builderBlockEntity;
        }
        return null;
    }

    @Override
    protected void initAndReadInventory(BuilderBlockEntity contentHolder) {

    }

    @Override
    protected void addSlots() {
        addSlot(new SlotItemHandler(contentHolder.inventory, 0, 55, 65));//编辑器
        addPlayerSlots(37, 161);
    }

    @Override
    protected void saveData(BuilderBlockEntity contentHolder) {

    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        Slot clickedSlot = getSlot(index);
        if (!clickedSlot.hasItem())
            return ItemStack.EMPTY;
        ItemStack stack = clickedSlot.getItem();
        if (index == 0)
            moveItemStackTo(stack, 2, slots.size(), true);
        else
            moveItemStackTo(stack, 0, 1, false);
        return ItemStack.EMPTY;
    }
}
