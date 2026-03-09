package org.shuangfa114.test.createtrackbuilder.foundation.util;

import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class Segment {
    public BlockPos pos;
    public Vec3 axis;

    public Segment(BlockPos pos) {
        this.pos = pos;
        this.axis = new Vec3(0,0,0);
    }
    public Segment(BlockPos pos, Vec3 axis) {
        this.pos = pos;
        this.axis = axis;
    }

    public static CompoundTag listToTag(List<Segment> segments,CompoundTag original) {
        ListTag listTag = new ListTag();
        for (Segment segment : segments) {
            CompoundTag sub = new CompoundTag();
            sub.put("position", NbtUtils.writeBlockPos(segment.pos));
            sub.put("axis", VecHelper.writeNBT(segment.axis));
            listTag.add(sub);
        }
        original.put("Segments", listTag);
        return original;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Segment segment) {
            return pos.equals(segment.pos) && axis.equals(segment.axis);
        }
        return false;
    }

    public static List<Segment> tagToList(CompoundTag tag) {
        List<Segment> segments = new ArrayList<>();
        if (tag != null && !tag.isEmpty()) {
            for (Tag seg : tag.getList("Segments", Tag.TAG_COMPOUND)) {
                if (seg instanceof CompoundTag compoundTag) {
                    segments.add(new Segment(NbtUtils.readBlockPos(compoundTag.getCompound("position"))
                            , VecHelper.readNBT(compoundTag.getList("axis",Tag.TAG_DOUBLE))));
                }
            }
        }
        return segments;
    }
}
