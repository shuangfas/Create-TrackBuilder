package org.shuangfa114.test.createtrackbuilder.foundation.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.trains.track.TrackShape;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBox;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.outliner.Outliner;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2d;

import java.util.Arrays;
import java.util.List;

public class Util {
    public static Vec3 getAxis(TrackShape shape) {
        return shape.getAxes().get(0);
    }

    public static Vector2d getAxisV2(TrackShape shape) {
        return toV2(getAxis(shape));
    }

    public static Vector2d toV2(Vec3 vec3) {
        return new Vector2d(vec3.x, vec3.z);
    }

    public static List<TrackShape> getFlatShapes() {
        return Arrays.asList(TrackShape.XO, TrackShape.ZO, TrackShape.ND, TrackShape.PD);
    }

    public static TrackShape getBestShape(Player player) {
        if (player != null) {
            Vec3 lookAngle = player.getLookAngle();
            return getBestShape(toV2(lookAngle));
        }
        return TrackShape.NONE;
    }

    /**
     * @param vec2 y always = 0
     */
    public static TrackShape getBestShape(Vector2d vec2) {
        if (Mth.equal(vec2.length(), 0))
            return TrackShape.ZO;
        TrackShape best = TrackShape.ZO;
        double bestValue = Float.MAX_VALUE;
        for (TrackShape shape : getFlatShapes()) {
            if (shape.isJunction() || shape.isPortal())
                continue;
            Vector2d axisV2 = getAxisV2(shape);
            double distance = Math.min(axisV2.distanceSquared(vec2), axisV2.normalize()
                    .mul(-1)
                    .distanceSquared(vec2));
            if (distance > bestValue)
                continue;
            bestValue = distance;
            best = shape;
        }
        return best;
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
