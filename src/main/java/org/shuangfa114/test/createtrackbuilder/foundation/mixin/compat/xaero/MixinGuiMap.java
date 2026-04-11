package org.shuangfa114.test.createtrackbuilder.foundation.mixin.compat.xaero;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.trains.track.TrackShape;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import org.joml.Vector2d;
import org.shuangfa114.test.createtrackbuilder.ModClient;
import org.shuangfa114.test.createtrackbuilder.api.structures.Segment;
import org.shuangfa114.test.createtrackbuilder.foundation.compat.xaero.CurveRenderer;
import org.shuangfa114.test.createtrackbuilder.foundation.compat.xaero.MapCompatClient;
import org.shuangfa114.test.createtrackbuilder.foundation.util.ModLang;
import org.shuangfa114.test.createtrackbuilder.foundation.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.lib.client.gui.widget.Tooltip;
import xaero.map.WorldMap;
import xaero.map.graphics.MapRenderHelper;
import xaero.map.gui.GuiMap;
import xaero.map.gui.GuiTexturedButton;
import xaero.map.gui.IRightClickableElement;
import xaero.map.gui.ScreenBase;

@Mixin(value = GuiMap.class, remap = false)
public abstract class MixinGuiMap extends ScreenBase implements IRightClickableElement {
    @Unique
    private byte intervalCounter = 0;
    @Unique
    private BlockPos doubleClickedPos;
    @Unique
    private TrackShape currentShape = TrackShape.NONE;
    @Shadow
    private int mouseBlockPosX;
    @Shadow
    private int mouseBlockPosY;
    @Shadow
    private int mouseBlockPosZ;

    protected MixinGuiMap(Screen parent, Screen escape, Component titleIn) {
        super(parent, escape, titleIn);
    }

    @Shadow
    public abstract <T extends GuiEventListener & Renderable & NarratableEntry> T addButton(T guiEventListener);

    @Unique
    @Inject(method = "init", at = @At("RETURN"))
    private void addEditingButton(CallbackInfo ci) {
        Button editingModeButton = new GuiTexturedButton(width - 20, height - 180, 20, 20, 165, 0, 16, 16,
                WorldMap.guiTextures, this::onEditBotton, () -> new Tooltip("switch"));
        editingModeButton.visible = ModClient.editorHandler.isActive();
        addButton(editingModeButton);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;endBatch()V", ordinal = 3))
    private void renderText(GuiGraphics guiGraphics, int scaledMouseX, int scaledMouseY, float partialTicks, CallbackInfo ci, @Local(ordinal = 4) VertexConsumer backgroundVertexBuffer) {
        if (MapCompatClient.isEditing) {
            Component component = ModLang.translateDirect("gui.map.editing");
            MapRenderHelper.drawCenteredStringWithBackground(guiGraphics, font, component, this.width / 2, this.height - 25, -1, 0, 0, 0, 0.3f, backgroundVertexBuffer);
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V"))
    private void renderCurve(GuiGraphics guiGraphics, int scaledMouseX, int scaledMouseY, float partialTicks, CallbackInfo ci, @Local PoseStack matrixStack) {
        GuiMap map = (GuiMap) (Object) this;
        if (MapCompatClient.isEditing) {
            CurveRenderer.prepareAndRender(guiGraphics,map,partialTicks);
        }
    }

    @Inject(method = "mapClicked", at = @At("HEAD"))
    private void onClick(int button, int x, int y, CallbackInfo ci) {
        if (MapCompatClient.isEditing) {
            if (doubleClickedPos != null) {
                ModClient.editorHandler.addSegment(new Segment(doubleClickedPos, currentShape));
                reset();
            }
            if (intervalCounter >= 0) {
                onDoubleClick(button, x, y);
            } else {
                intervalCounter = 3;
            }
        }
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void clickInterval(CallbackInfo ci) {
        if (intervalCounter >= 0) {
            intervalCounter--;
        }
    }

    @Unique
    private void onEditBotton(Button button) {
        MapCompatClient.isEditing = !MapCompatClient.isEditing;
    }

    @Unique
    private void onDoubleClick(int button, int x, int y) {
        doubleClickedPos = new BlockPos(mouseBlockPosX, mouseBlockPosY + 1, mouseBlockPosZ);
    }

    @Unique
    private void reset() {
        doubleClickedPos = null;
        currentShape = TrackShape.NONE;
    }
}
