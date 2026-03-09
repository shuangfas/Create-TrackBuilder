package org.shuangfa114.test.createtrackbuilder.foundation.util;

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
        return VecHelper.rotateCentered(VecHelper.voxelSpace(8 + offset, 16f, 7), stateAngle, Direction.Axis.Y);
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
}
