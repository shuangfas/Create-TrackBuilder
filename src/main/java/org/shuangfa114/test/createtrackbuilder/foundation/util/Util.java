package org.shuangfa114.test.createtrackbuilder.foundation.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.trains.track.TrackShape;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBox;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.outliner.Outliner;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.List;

public class Util {
    public static Vec3 getAxis(TrackShape shape) {
        return shape.getAxes().get(0);
    }

    public static List<TrackShape> getFlatShapes() {
        return Arrays.asList(TrackShape.XO, TrackShape.ZO, TrackShape.ND, TrackShape.PD);
    }

    public static TrackShape getBestShape(Player player) {
        if (player != null) {
            Vec3 lookAngle = player.getLookAngle();
            lookAngle = lookAngle.multiply(1, 0, 1);
            if (Mth.equal(lookAngle.length(), 0))
                lookAngle = VecHelper.rotate(new Vec3(0, 0, 1), -player.getYRot(), Direction.Axis.Y);
            lookAngle = lookAngle.normalize();
            TrackShape best = TrackShape.ZO;
            double bestValue = Float.MAX_VALUE;
            for (TrackShape shape : TrackShape.values()) {
                if (shape.isJunction() || shape.isPortal())
                    continue;
                Vec3 axis = shape.getAxes()
                        .get(0);
                double distance = Math.min(axis.distanceToSqr(lookAngle), axis.normalize()
                        .scale(-1)
                        .distanceToSqr(lookAngle));
                if (distance > bestValue)
                    continue;
                bestValue = distance;
                best = shape;
            }
            return best;
        }
        return TrackShape.NONE;
    }
    public static void showText(BlockPos blockPos, Object text, float offset) {
        AABB aabb = new AABB(blockPos).move(0, offset, 0);
        Outliner.getInstance().showOutline("debugText" + text
                , new ValueBox.TextValueBox(Component.literal("debug"), aabb, blockPos, Component.literal(text.toString()))
                        .transform(new ValueBoxTransform() {
                            @Override
                            public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
                                return new Vec3(0, 0, 0);
                            }

                            @Override
                            public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
                                float scale = 10;
                                TransformStack.of(ms)
                                        .translate(0, offset, 0)
                                        .scale(scale, scale, scale);
                            }
                        }));

    }
}
