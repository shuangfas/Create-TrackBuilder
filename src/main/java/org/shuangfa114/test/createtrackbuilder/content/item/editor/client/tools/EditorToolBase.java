package org.shuangfa114.test.createtrackbuilder.content.item.editor.client.tools;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.utility.RaycastHelper;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.render.SuperRenderTypeBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.shuangfa114.test.createtrackbuilder.CreateTrackBuilderClient;
import org.shuangfa114.test.createtrackbuilder.content.item.editor.client.EditorHandler;

public abstract class EditorToolBase implements IEditorTool {
    protected BlockPos selectedPos;
    protected Vec3 chasingSelectedPos;
    protected Vec3 lastChasingSelectedPos;
    protected EditorHandler editorHandler;
    protected boolean selectIgnoreBlocks;
    protected int selectionRange;

    @Override
    public void init() {
        editorHandler= CreateTrackBuilderClient.editorHandler;
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
    public void renderTool(PoseStack ms, SuperRenderTypeBuffer buffer, Vec3 camera) {

    }
}
