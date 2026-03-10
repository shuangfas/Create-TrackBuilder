package org.shuangfa114.test.createtrackbuilder.content.item.editor.client.tools;

import com.mojang.blaze3d.vertex.PoseStack;
import net.createmod.catnip.render.SuperRenderTypeBuffer;
import net.minecraft.world.phys.Vec3;

public interface IEditorTool {
    void init();

    void updateSelection();

    boolean handleClick(boolean left);

    boolean handleMouseWheel(double delta);

    void renderTool(PoseStack ms, SuperRenderTypeBuffer buffer, Vec3 camera);

    void tick();
}
