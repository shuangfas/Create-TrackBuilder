package org.shuangfa114.test.createtrackbuilder.content.item.editor.packet;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import org.shuangfa114.test.createtrackbuilder.content.item.editor.TrackEditor;
import org.shuangfa114.test.createtrackbuilder.foundation.util.algorithm.Segment;

import java.util.List;

//todo 去掉nbt的标签部分(即传递int)，优化网络性能
//todo 仅发送增量数据包
public class SegmentInitPacket extends SimplePacketBase {
    public List<Segment> segments;
    public int slot;

    public SegmentInitPacket(List<Segment> segments, int slot) {
        this.segments = segments;
        this.slot = slot;
    }

    public SegmentInitPacket(FriendlyByteBuf buf) {
        CompoundTag tag = buf.readNbt();
        segments = Segment.tagToList(tag);
        slot = buf.readInt();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeNbt(Segment.listToTag(segments, new CompoundTag()));
        buffer.writeInt(slot);
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            Player player = context.getSender();
            if (player == null) {
                return;
            }
            ItemStack stack;
            if (slot == -1) {
                stack = player.getMainHandItem();
            } else {
                stack = player.getInventory().getItem(slot);
            }
            if (stack.getItem() instanceof TrackEditor) {
                CompoundTag tag = stack.getOrCreateTag();
                Segment.listToTag(segments, tag);
                tag.putBoolean("Initialized",true);
            }
        });
        return true;
    }
}
