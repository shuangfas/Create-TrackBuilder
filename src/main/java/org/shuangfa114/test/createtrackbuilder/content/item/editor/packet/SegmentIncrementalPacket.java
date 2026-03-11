package org.shuangfa114.test.createtrackbuilder.content.item.editor.packet;

import com.simibubi.create.content.trains.track.TrackShape;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import org.shuangfa114.test.createtrackbuilder.content.item.editor.TrackEditor;
import org.shuangfa114.test.createtrackbuilder.foundation.util.algorithm.Segment;

public class SegmentIncrementalPacket extends SimplePacketBase {
    public Segment segment;
    public int slot;
    public int index;

    public SegmentIncrementalPacket(Segment segment, boolean deleted, int slot, int index) {
        this.segment = segment;
        this.slot = slot;
        this.index = index;
    }

    public SegmentIncrementalPacket(Segment segment, int slot) {
        this.segment = segment;
        this.slot = slot;
        this.index = -1;
    }

    public SegmentIncrementalPacket(FriendlyByteBuf buf) {
        this.segment = new Segment(buf.readBlockPos(), buf.readEnum(TrackShape.class));
        this.slot = buf.readInt();
        this.index = buf.readInt();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(this.segment.pos);
        buffer.writeEnum(this.segment.shape);
        buffer.writeInt(this.slot);
        buffer.writeInt(this.index);
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ItemStack stack = NetworkUtil.getItemStack(context, this.slot);
            if (stack.getItem() instanceof TrackEditor) {
                CompoundTag tag = stack.getOrCreateTag();
                ListTag listTag = tag.getList("Segments", Tag.TAG_COMPOUND);
                if (shouldDelete()) {
                    listTag.remove(this.index);
                    return;
                }
                if(listTag.isEmpty()){
                    listTag.add(this.segment.toTag());
                    tag.put("Segments", listTag);
                    return;
                }
                listTag.add(this.segment.toTag());
            }
        });
        return true;
    }

    private boolean shouldDelete() {
        return index >= 0;
    }
}
