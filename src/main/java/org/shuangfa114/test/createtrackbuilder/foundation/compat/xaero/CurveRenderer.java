package org.shuangfa114.test.createtrackbuilder.foundation.compat.xaero;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.mixin.compat.xaeros.XaeroFullscreenMapAccessor;
import net.createmod.catnip.data.Iterate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.shuangfa114.test.createtrackbuilder.ModClient;
import org.shuangfa114.test.createtrackbuilder.foundation.util.TrackPreview;
import org.shuangfa114.test.createtrackbuilder.foundation.util.Util;
import xaero.map.gui.GuiMap;

import java.util.ArrayList;


public class CurveRenderer {
    public static void prepareAndRender(GuiGraphics graphics, GuiMap screen, float pt) {
        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        float scale = getScale(screen);
        offsetTranslate(poseStack, screen, scale);
        TrackPreview.caches.values().forEach(info -> {
            renderCurve(graphics, info);
        });
        ModClient.getSegments().forEach(segment -> renderDirection(graphics, segment.pos, Util.getAxisV2(segment.shape)));
        poseStack.popPose();
    }

    public static void renderCurve(GuiGraphics graphics, TrackPreview.PlacementInfo info) {
        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        int color = info.valid ? 0xFF_95CD41 : 0xFF_EA5C2B;
        Vec3 v1 = info.end1;
        Vec3 a1 = info.axis1.normalize();
        Vec3 n1 = info.normal1.cross(a1)
                .scale(15 / 16f);
        Vec3 o1 = a1.scale(0.125f);
        Vec3 ex1 = a1.scale((info.end1Extent - (info.curve == null && info.end1Extent > 0 ? 2 : 0)) * info.axis1.length());
        VertexConsumer consumer = graphics.bufferSource().getBuffer(RenderType.lines());
        line(consumer, poseStack.last(), v1.add(n1), o1, ex1, color);
        line(consumer, poseStack.last(), v1.subtract(n1), o1, ex1, color);
        Vec3 v2 = info.end2;
        Vec3 a2 = info.axis2.normalize();
        Vec3 n2 = info.normal2.cross(a2).scale(15 / 16f);
        Vec3 o2 = a2.scale(0.125f);
        Vec3 ex2 = a2.scale(info.end2Extent * info.axis2.length());
        line(consumer, poseStack.last(), v2.add(n2), o2, ex2, color);
        line(consumer, poseStack.last(), v2.subtract(n2), o2, ex2, color);
        if (info.curve == null)
            return;
        for (boolean left : Iterate.trueAndFalse) {
            ArrayList<Vec3> vec3s = TrackPreview.calculateCurvePoints(info.curve, left);
            for (int i = 0; i < vec3s.size() - 1; i++) {
                poseStack.pushPose();
                Vec3 start = vec3s.get(i);
                Vec3 end = vec3s.get(i + 1);
                vertex(consumer, poseStack.last(), start.x, start.z, color);
                vertex(consumer, poseStack.last(), end.x, end.z, color);
                poseStack.popPose();
            }
        }
        graphics.bufferSource().endBatch();
        poseStack.popPose();
    }

    public static void renderDirection(GuiGraphics graphics, BlockPos pos, Vector2d vec2d) {
        Vector2f vec2f = new Vector2f((float) vec2d.x, (float) vec2d.y).normalize().mul(3);
        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        poseStack.translate(pos.getX(), pos.getZ(), 0);
        VertexConsumer consumer = graphics.bufferSource().getBuffer(RenderType.lines());
        PoseStack.Pose pose = poseStack.last();
        consumer.vertex(pose.pose(), -vec2f.x, -vec2f.y, 0).color(0xFF_FFFFFF).normal(pose.normal(), 2 * vec2f.x, 2 * vec2f.y, 0).endVertex();
        consumer.vertex(pose.pose(), vec2f.x, vec2f.y, 0).color(0xFF_FFFFFF).normal(pose.normal(), 2 * vec2f.x, 2 * vec2f.y, 0).endVertex();
        graphics.bufferSource().endBatch();
        poseStack.popPose();
    }

    private static void offsetTranslate(PoseStack poseStack, GuiMap screen, float scale) {
        poseStack.translate(screen.width / 2.0F, screen.height / 2.0F, 0);
        poseStack.scale(scale, scale, 1);
        poseStack.translate(-((XaeroFullscreenMapAccessor) screen).getCameraX(), -((XaeroFullscreenMapAccessor) screen).getCameraZ(), 0);
    }

    private static float getScale(GuiMap screen) {
        Minecraft mc = Minecraft.getInstance();
        Window window = mc.getWindow();
        double mapScale = ((XaeroFullscreenMapAccessor) screen).getScale();
        double guiScale = (double) window.getScreenWidth() / window.getGuiScaledWidth();
        return (float) (mapScale / guiScale);
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float z, int color) {
        consumer.vertex(pose.pose(), x, z, 0).color(color).normal(pose.normal(), x, z, 0).endVertex();
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, double x, double z, int color) {
        vertex(consumer, pose, (float) x, (float) z, color);
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, Vector2d vector2d, int color) {
        vertex(consumer, pose, (float) vector2d.x, (float) vector2d.y, color);
    }

    private static void line(VertexConsumer consumer, PoseStack.Pose pose, Vec3 v1, Vec3 o1, Vec3 ex, int color) {
        vertex(consumer, pose, Util.toV2(v1.subtract(o1)), color);
        vertex(consumer, pose, Util.toV2(v1.add(ex)), color);
    }
}
