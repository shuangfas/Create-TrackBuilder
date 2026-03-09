package org.shuangfa114.test.createtrackbuilder.content.item.editor.client.tools;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllKeys;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.outliner.AABBOutline;
import net.createmod.catnip.render.SuperRenderTypeBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.shuangfa114.test.createtrackbuilder.foundation.util.ModLang;
import org.shuangfa114.test.createtrackbuilder.foundation.util.VisualUtil;
import org.shuangfa114.test.createtrackbuilder.foundation.util.algorithm.Segment;

import java.util.ArrayList;

public class SelectionTool extends EditorToolBase {
    public static boolean enableAxis = false;

    @Override
    public void init() {
        super.init();
        selectionRange = 20;
    }

    @Override
    public void renderTool(PoseStack ms, SuperRenderTypeBuffer buffer, Vec3 camera) {
        super.renderTool(ms, buffer, camera);
        if (selectedPos != null) {
            VisualUtil.chaseAABB("chaseAABB", selectedPos);
        }
        //render selection
        AABBOutline outline = new AABBOutline(new AABB(new BlockPos(0,0,0)));
        outline.getParams().lineWidth(1/16f).colored(0x6886c5);
        ms.pushPose();
        for(Segment segment: editorHandler.segments){
            outline.setBounds(new AABB(segment.pos));
            outline.render(ms,buffer,camera, AnimationTickHolder.getPartialTicks());
        }
        ms.popPose();
    }

    @Override
    public boolean handleClick(boolean left) {
        if (!left) {
            if (AllKeys.ctrlDown()) {
                if (editorHandler.segments.size() < 2) {
                    ModLang.translate("selection.not_enough");
                    return false;
                }
                ItemStack stack = editorHandler.getActiveEditorItem();
                CompoundTag tag = stack.getOrCreateTag();
                Segment.listToTag(editorHandler.segments, tag);
                editorHandler.sync();
                editorHandler.deploy();
                return true;
            }
            editorHandler.segments.add(new Segment(selectedPos));
            return true;
        }
        return false;
    }
}
