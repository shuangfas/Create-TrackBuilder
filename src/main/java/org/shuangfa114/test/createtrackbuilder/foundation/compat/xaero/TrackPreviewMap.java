package org.shuangfa114.test.createtrackbuilder.foundation.compat.xaero;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.mixin.compat.xaeros.XaeroFullscreenMapAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.shuangfa114.test.createtrackbuilder.CreateTrackBuilderClient;
import org.shuangfa114.test.createtrackbuilder.api.structures.Segment;
import xaero.map.graphics.CustomRenderTypes;
import xaero.map.gui.GuiMap;
import xaero.map.misc.Misc;

import java.util.List;

public class TrackPreviewMap {
    public static void renderCurve(GuiGraphics graphics, GuiMap screen, float pt) {

    }

    public static void renderSegments(GuiGraphics graphics, GuiMap screen, float pt) {
        Minecraft mc = Minecraft.getInstance();
        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        float scale = getScale(screen);
        offsetTranslate(poseStack, screen, scale);
        int packedLight = LightTexture.FULL_BRIGHT;
        List<Segment> segments = CreateTrackBuilderClient.editorHandler.segments;
        for (int i = 0; i < segments.size(); i++) {
            int[] c = getColor(i == 0 || i == segments.size() - 1);
            Segment segment = segments.get(i);
            float x = (float) segment.pos.getX();
            float z = (float) segment.pos.getZ();
            poseStack.pushPose();
            poseStack.translate(x, z, 0);
            Matrix4f matrix4f = poseStack.last().pose();
            VertexConsumer colorVertex = (graphics.bufferSource().getBuffer(CustomRenderTypes.MAP_COLOR_OVERLAY));
            colorVertex.vertex(matrix4f, 0, 0, 0).color(c[0], c[1], c[2], c[3]).uv(0, 0).uv2(packedLight).endVertex();
            colorVertex.vertex(matrix4f, 0, 1, 0).color(c[0], c[1], c[2], c[3]).uv(0, 1).uv2(packedLight).endVertex();
            colorVertex.vertex(matrix4f, 1, 1, 0).color(c[0], c[1], c[2], c[3]).uv(1, 1).uv2(packedLight).endVertex();
            colorVertex.vertex(matrix4f, 1, 0, 0).color(c[0], c[1], c[2], c[3]).uv(1, 0).uv2(packedLight).endVertex();
            float inv = 1 / scale;
            poseStack.translate(0.5 + inv * 2.36, 0.49 - inv * 0.94, 0);
            poseStack.scale(inv / 2, inv / 2, 1);
            matrix4f = poseStack.last().pose();
            VertexConsumer textureVertex = graphics.bufferSource().getBuffer(CustomRenderTypes.GUI_NEAREST);
            GuiMap.renderTexturedModalRect(matrix4f, textureVertex, -19, -40, 36, 35, 28, 41, 256, 256, 1, 1, 1, 0.8F);
            graphics.bufferSource().endBatch();
            poseStack.scale(4F, 4F, 1);
            String name = String.valueOf(i);
            Misc.drawNormalText(poseStack, name, -2F - mc.font.width(name) / 3F, -10, 0xFF_FFFFFF, true, graphics.bufferSource());
            poseStack.popPose();
        }
        poseStack.popPose();
    }

    public static void renderDirection(GuiGraphics graphics, GuiMap screen, BlockPos pos, Vector2d vec2d, float pt) {
        Vector2f vec2f = new Vector2f((float) vec2d.x, (float) vec2d.y).normalize().mul(3);
        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        float scale = getScale(screen);
        offsetTranslate(poseStack, screen, scale);
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

    private static int[] getColor(boolean end) {
        int[] color = new int[4];
        color[0] = end ? 218 : 107;
        color[1] = end ? 165 : 142;
        color[2] = end ? 32 : 35;
        color[3] = 239;
        return color;
    }
}
