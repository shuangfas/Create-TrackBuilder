package org.shuangfa114.test.createtrackbuilder.content.item.editor.packet;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import org.shuangfa114.test.createtrackbuilder.content.item.editor.TrackEditor;


public class SegmentInitPacket extends SimplePacketBase {
    public int slot;

    public SegmentInitPacket(int slot) {
        this.slot = slot;
    }

    public SegmentInitPacket(FriendlyByteBuf buf) {
        slot = buf.readInt();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(slot);
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ItemStack stack = NetworkUtil.getItemStack(context,this.slot);
            if (stack.getItem() instanceof TrackEditor) {
                CompoundTag tag = stack.getOrCreateTag();
                tag.putBoolean("Initialized",true);
            }
        });
        return true;
    }
}
