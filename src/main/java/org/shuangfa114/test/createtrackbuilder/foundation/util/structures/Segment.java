package org.shuangfa114.test.createtrackbuilder.foundation.util.structures;

import com.simibubi.create.content.trains.track.TrackShape;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.phys.Vec3;

import java.util.LinkedList;
import java.util.List;

public class Segment {
    public BlockPos pos;
    public TrackShape shape;

    public Segment(BlockPos pos) {
        this.pos = pos;
        this.shape = TrackShape.NONE;
    }

    public Segment(BlockPos pos, TrackShape shape) {
        this.pos = pos;
        this.shape = shape;
    }

    public static CompoundTag listToTag(List<Segment> segments, CompoundTag original) {
        ListTag listTag = new ListTag();
        for (Segment segment : segments) {
            CompoundTag sub = new CompoundTag();
            sub.put("Position", NbtUtils.writeBlockPos(segment.pos));
            sub.putString("Shape", segment.shape.name());
            listTag.add(sub);
        }
        original.put("Segments", listTag);
        return original;
    }

    public static List<Segment> tagToList(CompoundTag tag) {
        List<Segment> segments = new LinkedList<>();
        if (tag != null && !tag.isEmpty()) {
            for (Tag seg : tag.getList("Segments", Tag.TAG_COMPOUND)) {
                if (seg instanceof CompoundTag compoundTag) {
                    segments.add(new Segment(NbtUtils.readBlockPos(compoundTag.getCompound("Position"))
                            , TrackShape.valueOf(compoundTag.getString("Shape"))));
                }
            }
        }
        return segments;
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.put("Position", NbtUtils.writeBlockPos(this.pos));
        tag.putString("Shape", this.shape.name());
        return tag;
    }

    public static Segment fromTag(CompoundTag tag) {
        return new Segment(NbtUtils.readBlockPos(tag.getCompound("Position")), TrackShape.valueOf(tag.getString("Shape")));
    }

    public Vec3 getAxis() {
        return this.shape.getAxes().get(0);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Segment segment) {
            return pos.equals(segment.pos) && shape.equals(segment.shape);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return pos.hashCode() ^ shape.hashCode();
    }
}
