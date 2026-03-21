package org.shuangfa114.test.createtrackbuilder.foundation.packet.editor;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import org.shuangfa114.test.createtrackbuilder.content.item.editor.TrackEditor;
import org.shuangfa114.test.createtrackbuilder.foundation.packet.NetworkUtil;

public class OwnerSettingPacket extends SimplePacketBase {
    public int slot;
    public String owner;

    public OwnerSettingPacket(FriendlyByteBuf buf) {
        slot = buf.readInt();
        owner = buf.readUtf();
    }

    public OwnerSettingPacket(int slot, String owner) {
        this.slot = slot;
        this.owner = owner;
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(slot);
        buffer.writeUtf(owner);
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ItemStack stack = NetworkUtil.getItemStack(context, this.slot);
            if (stack.getItem() instanceof TrackEditor) {
                CompoundTag tag = stack.getOrCreateTag();
                tag.putString("Owner", owner);
            }
        });
        return true;
    }
}
