package org.shuangfa114.test.createtrackbuilder.foundation.compat.xaero.element;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureManager;
import org.shuangfa114.test.createtrackbuilder.api.structures.Segment;
import org.shuangfa114.test.createtrackbuilder.foundation.compat.xaero.MapCompatClient;
import xaero.map.WorldMap;
import xaero.map.element.MapElementRenderer;
import xaero.map.element.render.ElementRenderInfo;
import xaero.map.element.render.ElementRenderLocation;
import xaero.map.graphics.CustomRenderTypes;
import xaero.map.graphics.MapRenderHelper;
import xaero.map.graphics.renderer.multitexture.MultiTextureRenderTypeRendererProvider;

public class SegmentRenderer extends MapElementRenderer<Segment, SegmentRenderContext, SegmentRenderer> {
    protected SegmentRenderer(SegmentRenderContext context, SegmentRenderProvider provider, SegmentReader reader) {
        super(context, provider, reader);
    }

    public static SegmentRenderer create() {
        return new SegmentRenderer(new SegmentRenderContext(), new SegmentRenderProvider(), new SegmentReader());
    }

    @Override
    public boolean renderElement(Segment element, boolean hovered, double optionalDepth, float optionalScale, double partialX, double partialY, ElementRenderInfo renderInfo, GuiGraphics guiGraphics, MultiBufferSource.BufferSource vanillaBufferSource, MultiTextureRenderTypeRendererProvider rendererProvider) {
        PoseStack matrixStack = guiGraphics.pose();
        matrixStack.translate(partialX, partialY, 0.0F);
        int flagU = 35;
        int flagV = 34;
        int flagW = 30;
        int flagH = 43;
        matrixStack.translate((float) (-flagW) / 2.0F, (float) (-flagH + 1), 0.0F);
        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
        MapRenderHelper.blitIntoMultiTextureRenderer(matrixStack.last().pose(), this.context.uniqueTextureUIObjectRenderer, 0.0F, 0.0F, flagU, flagV, flagW, flagH, 1, 1, 1, 0.9F, textureManager.getTexture(WorldMap.guiTextures).getId());
        return false;
    }

    @Override
    public void renderElementShadow(Segment element, boolean hovered, float optionalScale, double partialX, double partialY, ElementRenderInfo renderInfo, GuiGraphics guiGraphics, MultiBufferSource.BufferSource vanillaBufferSource, MultiTextureRenderTypeRendererProvider rendererProvider) {
        PoseStack matrixStack = guiGraphics.pose();
        matrixStack.translate(partialX, partialY, 0.0F);
        MapRenderHelper.blitIntoExistingBuffer(matrixStack.last().pose(), this.context.regularUIObjectConsumer, 0, 19, 0, 117, 41, 22, 0.0F, 0.0F, 0.0F, 0.9F);
    }

    @Override
    public void preRender(ElementRenderInfo renderInfo, MultiBufferSource.BufferSource vanillaBufferSource, MultiTextureRenderTypeRendererProvider rendererProvider, boolean shadow) {
        this.context.regularUIObjectConsumer = vanillaBufferSource.getBuffer(CustomRenderTypes.GUI_BILINEAR);
        this.context.textBGConsumer = vanillaBufferSource.getBuffer(CustomRenderTypes.MAP_ELEMENT_TEXT_BG);
        this.context.uniqueTextureUIObjectRenderer = rendererProvider.getRenderer((t) -> RenderSystem.setShaderTexture(0, t), MultiTextureRenderTypeRendererProvider::defaultTextureBind, CustomRenderTypes.GUI_BILINEAR_PREMULTIPLIED);
    }

    @Override
    public void postRender(ElementRenderInfo renderInfo, MultiBufferSource.BufferSource vanillaBufferSource, MultiTextureRenderTypeRendererProvider rendererProvider, boolean shadow) {
        MultiBufferSource.BufferSource renderTypeBuffers = WorldMap.worldMapClientOnly.customVertexConsumers.getRenderTypeBuffers();
        rendererProvider.draw(this.context.uniqueTextureUIObjectRenderer);
        renderTypeBuffers.endBatch();
    }

    @Override
    public boolean shouldRender(ElementRenderLocation location, boolean shadow) {
        return MapCompatClient.isEditing;
    }

    @Override
    public int getOrder() {
        return 201;
    }

    //————————————————————
    @Override
    public void beforeRender(int i, Minecraft minecraft, GuiGraphics guiGraphics, double v, double v1, double v2, double v3, float v4, double v5, double v6, TextureManager textureManager, Font font, MultiBufferSource.BufferSource bufferSource, MultiTextureRenderTypeRendererProvider multiTextureRenderTypeRendererProvider, boolean b) {

    }

    @Override
    public void afterRender(int i, Minecraft minecraft, GuiGraphics guiGraphics, double v, double v1, double v2, double v3, float v4, double v5, double v6, TextureManager textureManager, Font font, MultiBufferSource.BufferSource bufferSource, MultiTextureRenderTypeRendererProvider multiTextureRenderTypeRendererProvider, boolean b) {

    }

    @Override
    public void renderElementPre(int i, Segment segment, boolean b, Minecraft minecraft, GuiGraphics guiGraphics, double v, double v1, double v2, double v3, float v4, double v5, double v6, TextureManager textureManager, Font font, MultiBufferSource.BufferSource bufferSource, MultiTextureRenderTypeRendererProvider multiTextureRenderTypeRendererProvider, float v7, double v8, double v9, boolean b1, float v10) {

    }

    @Override
    public boolean renderElement(int i, Segment segment, boolean b, Minecraft minecraft, GuiGraphics guiGraphics, double v, double v1, double v2, double v3, float v4, double v5, double v6, TextureManager textureManager, Font font, MultiBufferSource.BufferSource bufferSource, MultiTextureRenderTypeRendererProvider multiTextureRenderTypeRendererProvider, int i1, double v7, float v8, double v9, double v10, boolean b1, float v11) {
        return false;
    }

    @Override
    public boolean shouldRender(int i, boolean b) {
        return false;
    }

}
