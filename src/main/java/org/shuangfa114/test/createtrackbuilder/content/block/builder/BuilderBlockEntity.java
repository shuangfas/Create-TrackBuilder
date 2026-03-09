package org.shuangfa114.test.createtrackbuilder.content.block.builder;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.logistics.filter.FilterItem;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.shuangfa114.test.createtrackbuilder.content.item.editor.TrackEditor;
import org.shuangfa114.test.createtrackbuilder.foundation.blockEntity.behaviour.ItemPlacingBehaviour;
import org.shuangfa114.test.createtrackbuilder.foundation.util.BuilderTransform;

import java.util.List;

public class BuilderBlockEntity extends KineticBlockEntity {
    public int targetX;
    public int targetZ;
    FilteringBehaviour filter;
    ItemPlacingBehaviour editor;

    public BuilderBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(filter = new FilteringBehaviour(this,new BuilderTransform(-5)));
        behaviours.add(editor = new ItemPlacingBehaviour(this,new BuilderTransform(5)));
        filter.withPredicate(itemStack -> !(itemStack.getItem() instanceof FilterItem||itemStack.getItem() instanceof TrackEditor));
        editor.withPredicate(itemStack -> itemStack.getItem() instanceof TrackEditor);
        super.addBehaviours(behaviours);
    }

    @Override
    public float calculateStressApplied() {
        return 32;
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        compound.putInt("target_x", targetX);
        compound.putInt("target_z", targetZ);
        super.write(compound, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        targetX = compound.getInt("target_x");
        targetZ = compound.getInt("target_z");
        super.read(compound, clientPacket);
    }


    public int getTargetX() {
        return targetX;
    }

    public void setTargetX(int targetX) {
        this.targetX = targetX;
    }

    public int getTargetZ() {
        return targetZ;
    }

    public void setTargetZ(int targetZ) {
        this.targetZ = targetZ;
    }
}
