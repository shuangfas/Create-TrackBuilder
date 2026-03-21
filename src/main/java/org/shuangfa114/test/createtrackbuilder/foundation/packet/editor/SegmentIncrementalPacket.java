package org.shuangfa114.test.createtrackbuilder.foundation.packet.editor;

import com.simibubi.create.content.trains.track.TrackShape;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import org.shuangfa114.test.createtrackbuilder.content.item.editor.TrackEditor;
import org.shuangfa114.test.createtrackbuilder.foundation.packet.NetworkUtil;
import org.shuangfa114.test.createtrackbuilder.foundation.util.structures.Segment;

public class SegmentIncrementalPacket extends SimplePacketBase {
    public Segment segment;
    public int slot;
    public Operation operation;
    public int index;

    public SegmentIncrementalPacket(Segment segment, int slot, Operation operation, int index) {
        this.segment = segment;
        this.slot = slot;
        this.operation = operation;
        this.index = index;
    }

    public SegmentIncrementalPacket(Segment segment, int slot) {
        this.segment = segment;
        this.slot = slot;
        this.operation = Operation.ADD;
        this.index = -1;
    }

    public SegmentIncrementalPacket(FriendlyByteBuf buf) {
        this.segment = new Segment(buf.readBlockPos(), buf.readEnum(TrackShape.class));
        this.slot = buf.readInt();
        this.operation = buf.readEnum(Operation.class);
        this.index = buf.readInt();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(this.segment.pos);
        buffer.writeEnum(this.segment.shape);
        buffer.writeInt(this.slot);
        buffer.writeEnum(this.operation);
        buffer.writeInt(this.index);
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ItemStack stack = NetworkUtil.getItemStack(context, this.slot);
            if (stack.getItem() instanceof TrackEditor) {
                ListTag listTag = stack.getOrCreateTag().getList("Segments", Tag.TAG_COMPOUND);
                switch (this.operation) {
                    case ADD:
                        listTag.add(this.segment.toTag());
                        break;
                    case MODIFY:
                        listTag.set(index, this.segment.toTag());
                        break;
                    case REMOVE:
                        listTag.remove(index);
                }
            }
        });
        return true;
    }

    public enum Operation {ADD, MODIFY, REMOVE}
}
