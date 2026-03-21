package org.shuangfa114.test.createtrackbuilder.foundation.blockEntity.behaviour;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.shuangfa114.test.createtrackbuilder.content.block.builder.BuilderBlock;

public class BuilderTransform extends ValueBoxTransform {
    public float offset;

    public BuilderTransform(float offset) {
        this.offset = offset;
    }

    @Override
    public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
        float stateAngle = AngleHelper.horizontalAngle(getDirection(state));
        return VecHelper.rotateCentered(VecHelper.voxelSpace(8 , 16f, 8+ offset), stateAngle, Direction.Axis.Y);
    }

    @Override
    public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
        float yRot = AngleHelper.horizontalAngle(getDirection(state)) + 180;
        TransformStack.of(ms)
                .rotateYDegrees(yRot)
                .rotateXDegrees(90);
    }

    Direction getDirection(BlockState state) {
        Direction.Axis axis = state.getValue(BuilderBlock.HORIZONTAL_AXIS);
        return axis == Direction.Axis.X ? Direction.WEST : Direction.SOUTH;
    }

    @Override
    public boolean testHit(LevelAccessor level, BlockPos pos, BlockState state, Vec3 localHit) {
        Vec3 offset = getLocalOffset(level, pos, state);
        if (offset == null)
            return false;
        return localHit.distanceTo(offset) < scale / 3;
    }
}
