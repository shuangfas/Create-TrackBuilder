package org.shuangfa114.test.createtrackbuilder.content.item.editor.client.tools;

import com.mojang.blaze3d.vertex.PoseStack;
import net.createmod.catnip.render.SuperRenderTypeBuffer;
import net.minecraft.world.phys.Vec3;
import org.shuangfa114.test.createtrackbuilder.foundation.util.VisualUtil;

public class NewSegmentTool extends EditorToolBase{
    @Override
    public void renderTool(PoseStack ms, SuperRenderTypeBuffer buffer, Vec3 camera) {
        super.renderTool(ms, buffer, camera);
        VisualUtil.chaseAABB("chaseAABB", selectedPos);
    }
}
