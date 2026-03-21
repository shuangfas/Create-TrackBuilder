package org.shuangfa114.test.createtrackbuilder.foundation.packet;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public class NetworkUtil {
    public static ItemStack getItemStack(NetworkEvent.Context context, int slot) {
        Player player = context.getSender();
        if (player == null) {
            return ItemStack.EMPTY;
        }
        ItemStack stack;
        if (slot == -1) {
            stack = player.getMainHandItem();
        } else {
            stack = player.getInventory().getItem(slot);
        }
        return stack;
    }
}
