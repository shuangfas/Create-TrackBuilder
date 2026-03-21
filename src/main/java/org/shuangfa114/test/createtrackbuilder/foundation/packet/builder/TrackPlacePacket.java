package org.shuangfa114.test.createtrackbuilder.foundation.packet.builder;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.network.NetworkEvent;
import org.shuangfa114.test.createtrackbuilder.content.block.builder.BuilderBlockEntity;

public class TrackPlacePacket extends SimplePacketBase {
    public BlockPos pos;

    public TrackPlacePacket(BlockPos pos) {
        this.pos = pos;
    }

    public TrackPlacePacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ServerLevel level = (ServerLevel) context.getSender().level();
            if (level.getBlockEntity(pos) instanceof BuilderBlockEntity blockEntity) {
                blockEntity.saveAndTransmit();
            }
        });
        return true;
    }
}
