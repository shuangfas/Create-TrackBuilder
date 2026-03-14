package org.shuangfa114.test.createtrackbuilder.content.item.editor.client.tools;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllSpecialTextures;
import com.simibubi.create.content.trains.track.TrackShape;
import com.simibubi.create.foundation.utility.RaycastHelper;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.outliner.Outliner;
import net.createmod.catnip.render.SuperRenderTypeBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.shuangfa114.test.createtrackbuilder.CreateTrackBuilderClient;
import org.shuangfa114.test.createtrackbuilder.content.item.editor.client.EditorHandler;
import org.shuangfa114.test.createtrackbuilder.foundation.util.algorithm.Segment;

import java.util.Iterator;

public abstract class EditorToolBase implements IEditorTool {
    private static final ItemStack indicator = new ItemStack(AllItems.BRASS_HAND);
    protected BlockPos selectedPos;
    protected Vec3 chasingSelectedPos;
    protected Vec3 lastChasingSelectedPos;
    protected EditorHandler handler;
    protected boolean selectIgnoreBlocks;
    protected int selectionRange;
    float rotationAmount;

    @Override
    public void init() {
        handler = CreateTrackBuilderClient.editorHandler;
    }

    @Override
    public void updateSelection() {
        updateTargetPos();
        if (selectedPos == null)
            return;
        lastChasingSelectedPos = chasingSelectedPos;
        Vec3 target = Vec3.atLowerCornerOf(selectedPos);
        if (target.distanceTo(chasingSelectedPos) < 1 / 512f) {
            chasingSelectedPos = target;
            return;
        }
        chasingSelectedPos = chasingSelectedPos.add(target.subtract(chasingSelectedPos)
                .scale(1 / 2f));
    }

    public void updateTargetPos() {
        LocalPlayer player = Minecraft.getInstance().player;
        boolean snap = this.selectedPos == null;
        // Select location at distance
        if (selectIgnoreBlocks) {
            float pt = AnimationTickHolder.getPartialTicks();
            selectedPos = BlockPos.containing(player.getEyePosition(pt)
                    .add(player.getLookAngle()
                            .scale(selectionRange)));
            if (snap)
                lastChasingSelectedPos = chasingSelectedPos = Vec3.atLowerCornerOf(selectedPos);
            return;
        }

        // Select targeted Block
        selectedPos = null;
        BlockHitResult trace = RaycastHelper.rayTraceRange(player.level(), player, 75);
        if (trace == null || trace.getType() != HitResult.Type.BLOCK)
            return;
        selectedPos = BlockPos.containing(trace.getLocation());
        if (snap)
            lastChasingSelectedPos = chasingSelectedPos = Vec3.atLowerCornerOf(selectedPos);
    }

    @Override
    public boolean handleClick(boolean left) {
        return false;
    }

    @Override
    public boolean handleMouseWheel(double delta) {
        return false;
    }

    @Override
    public void renderTool(PoseStack ms, SuperRenderTypeBuffer buffer, Vec3 camera) {//fuck math fuck render fuck everything i can see
        rotationAmount = (AnimationTickHolder.getPartialTicks()/10 + rotationAmount) % 10;
        Minecraft mc = Minecraft.getInstance();
        Vec3 axis = Vec3.ZERO;
        for (Segment segment : handler.segments) {
            if (segment.shape == TrackShape.NONE) {
                continue;
            }
            Vec3 centre2 = segment.pos.getCenter().subtract(0,0.4,0).subtract(camera);
            axis = segment.getAxis();
            ms.pushPose();
            ms.translate(centre2.x, centre2.y, centre2.z);
            for(boolean t: Iterate.falseAndTrue){
                ms.pushPose();
                correctDirection(ms,axis,t);
                renderIndicator(ms,buffer,mc);
                ms.popPose();
            }
            ms.popPose();
        }
        Minecraft.getInstance().player.displayClientMessage(Component.literal(axis + "=" + Math.toDegrees(getRadiansOfAxis(axis))), true);
    }

    @Override
    public void tick() {
        if (selectedPos != null && shouldShowSelection()) {
            Outliner.getInstance().chaseAABB("editor_selection", getSelectionAABB())
                    .lineWidth(1 / 16f)
                    .colored(0x6886c5)
                    .withFaceTextures(AllSpecialTextures.CHECKERED, AllSpecialTextures.HIGHLIGHT_CHECKERED);
        }
        Segment lastSeg = null;
        //render selection
        for (int i = 0; i < handler.segments.size(); i++) {
            Segment seg = handler.segments.get(i);
            if (i > 0) {
                Outliner.getInstance().showLine("segmentConnection" + i, seg.pos.getCenter(), lastSeg.pos.getCenter());
            }
            Outliner.getInstance().showAABB("segment:" + seg.pos, new AABB(seg.pos))
                    .colored(i == 0 || i == handler.segments.size() - 1 ? 0xDAA520 : 0x6B8E23)
                    .lineWidth(1 / 16f);
            lastSeg = seg;
        }
    }

    public boolean shouldShowSelection() {
        return false;
    }

    public AABB getSelectionAABB() {
        return new AABB(selectedPos);
    }

    public float getRadiansOfAxis(Vec3 axis) {
        return (float) Math.atan2(-axis.z, axis.x);
    }
    public void renderIndicator(PoseStack ms, SuperRenderTypeBuffer buffer, Minecraft mc) {
        mc.getItemRenderer().render(indicator, ItemDisplayContext.FIXED, false, ms,
                buffer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY,
                mc.getItemRenderer().getModel(indicator, null, null, 0));
    }
    public void correctDirection(PoseStack ms,Vec3 axis,boolean negative){
        TransformStack.of(ms)
                .translate(axis.normalize().scale(negative ? -.4 : .4))
                .rotateZDegrees(90)
                .rotateYDegrees(90)
                .rotateZDegrees(135)
                .rotateZ(getRadiansOfAxis(axis))
                .rotateZDegrees(negative?180:0);
    }
}
