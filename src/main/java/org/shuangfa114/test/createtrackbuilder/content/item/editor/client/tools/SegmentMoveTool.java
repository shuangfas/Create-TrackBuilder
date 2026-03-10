package org.shuangfa114.test.createtrackbuilder.content.item.editor.client.tools;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.shuangfa114.test.createtrackbuilder.foundation.util.ModLang;

import java.util.Optional;

public class SegmentMoveTool extends EditorToolBase {
    public boolean selected;
    public int index = -1;

    @Override
    public boolean handleClick(boolean left) {
        if (!left&&selectedPos != null) {
            if (selected && index != -1) {
                editorHandler.segments.get(index).pos = selectedPos;
                ModLang.translate("tool.move.moved").sendStatus(Minecraft.getInstance().player);
                selected = false;
                index = -1;
                return true;
            }
            ModLang.translate("tool.move.selected").sendStatus(Minecraft.getInstance().player);
            selected = true;
            return true;
        }
        return false;
    }

    @Override
    public void updateTargetPos() {
        super.updateTargetPos();
        if (!selected) {
            for (int i = 0; i < editorHandler.segments.size(); i++) {
                BlockPos target = editorHandler.segments.get(i).pos;
                Vec3 eyeVec3 = Minecraft.getInstance().cameraEntity.getEyePosition();
                if (eyeVec3.distanceTo(target.getCenter()) > eyeVec3.distanceTo(selectedPos.getCenter())) {
                    continue;//当节点与摄像头之间被阻挡时
                }
                AABB aabb = new AABB(editorHandler.segments.get(i).pos);
                Optional<Vec3> result = aabb.clip(eyeVec3, eyeVec3.add(Minecraft.getInstance().player.getLookAngle().normalize().scale(75)));
                if (result.isPresent()) {
                    selectedPos = target;
                    index = i;
                    return;
                }
            }
            selectedPos = null;
        }
    }

    @Override
    public boolean shouldShowSelection() {
        return selectedPos != null;
    }

    @Override
    public AABB getSelectionAABB() {
        return super.getSelectionAABB().inflate(0.05);
    }
}
