package org.shuangfa114.test.createtrackbuilder.foundation.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllSpecialTextures;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBox;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.outliner.Outliner;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.shuangfa114.test.createtrackbuilder.CreateTrackBuilder;

@OnlyIn(Dist.CLIENT)
public class VisualUtil {
    //onlyDebug
    public static void liner(int id, Vec3 start, Vec3 end) {
        showLine(start, end, getColorForNumber(id));
    }

    public static void linerAdd(int id, Vec3 start, Vec3 addon) {
        liner(id, start, start.add(addon));
    }

    public static void showLine(Vec3 start, Vec3 end, int color) {
        Outliner.getInstance().showLine(withModid("debugLine" + start + end), start, end)
                .colored(color)
                .disableLineNormals()
                .lineWidth(1 / 16f);
    }
    public static void chaseAABB(String id,BlockPos pos) {
        Outliner.getInstance().chaseAABB(CreateTrackBuilder.MODID + id, new AABB(pos))
                .colored(0x6886c5)
                .lineWidth(1 / 16f)
                .withFaceTextures(AllSpecialTextures.CHECKERED, AllSpecialTextures.HIGHLIGHT_CHECKERED);
    }

    public static void showText(BlockPos blockPos, Object text, float offset) {
        AABB aabb = new AABB(blockPos).move(0, offset, 0);
        Outliner.getInstance().showOutline(withModid("debugText" + text)
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

    public static String withModid(String text) {
        return CreateTrackBuilder.MODID + text;
    }

    public static int getColorForNumber(int x) {
        // 色调：0~360°，均匀分布
        float hue = (x - 1) * 18.0f;
        // 固定饱和度和亮度（100%饱和，50%亮度，颜色最鲜艳）
        float saturation = 1.0f;
        float lightness = 0.5f;

        int[] rgb = hslToRgb(hue, saturation, lightness);
        return (rgb[0] << 16) | (rgb[1] << 8) | rgb[2];
    }

    private static int[] hslToRgb(float h, float s, float l) {
        float c = (1 - Math.abs(2 * l - 1)) * s;           // 色度
        float hPrime = h / 60.0f;                          // 将H映射到[0,6)区间
        float x = c * (1 - Math.abs(hPrime % 2 - 1));      // 中间值
        float r1, g1, b1;
        if (hPrime < 1) {
            r1 = c;
            g1 = x;
            b1 = 0;
        } else if (hPrime < 2) {
            r1 = x;
            g1 = c;
            b1 = 0;
        } else if (hPrime < 3) {
            r1 = 0;
            g1 = c;
            b1 = x;
        } else if (hPrime < 4) {
            r1 = 0;
            g1 = x;
            b1 = c;
        } else if (hPrime < 5) {
            r1 = x;
            g1 = 0;
            b1 = c;
        } else {
            r1 = c;
            g1 = 0;
            b1 = x;
        }
        float m = l - c / 2;                                // 亮度调节量
        int r = Math.round((r1 + m) * 255);
        int g = Math.round((g1 + m) * 255);
        int b = Math.round((b1 + m) * 255);
        return new int[]{r, g, b};
    }
}
