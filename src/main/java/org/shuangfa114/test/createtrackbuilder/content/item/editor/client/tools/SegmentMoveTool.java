package org.shuangfa114.test.createtrackbuilder.content.item.editor.client.tools;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.shuangfa114.test.createtrackbuilder.foundation.packet.editor.SegmentIncrementalPacket;
import org.shuangfa114.test.createtrackbuilder.foundation.util.ModLang;
import org.shuangfa114.test.createtrackbuilder.foundation.util.TrackPreview;
import org.shuangfa114.test.createtrackbuilder.foundation.util.Util;
import org.shuangfa114.test.createtrackbuilder.api.structures.Segment;

import java.util.Optional;

public class SegmentMoveTool extends EditorToolBase {
    public boolean selected;
    public int index = -1;

    @Override
    public boolean handleClick(boolean left) {
        if (!left && selectedPos != null) {
            if (selected && index != -1) {
                Segment origin = handler.segments.get(index);
                Segment seg = new Segment(selectedPos, Util.getBestShape(Minecraft.getInstance().player));

//                if (index - 1 >= 0) {
//                    SegmentEdge before = new SegmentEdge(handler.segments.get(index - 1), origin);
//                    TrackPreview.change(before, new SegmentEdge(handler.segments.get(index - 1), seg));
//                }
//                if (index + 1 < handler.segments.size()) {
//                    SegmentEdge after = new SegmentEdge(origin, handler.segments.get(index + 1));
//                    TrackPreview.change(after, new SegmentEdge(seg, handler.segments.get(index + 1)));
//                }

                handler.segments.set(index, seg);
                ModLang.translate("editor.tool.move.moved").sendStatus(Minecraft.getInstance().player);
                handler.sync(new SegmentIncrementalPacket(seg, handler.getActiveHotbarSlot(), SegmentIncrementalPacket.Operation.MODIFY, index));
                TrackPreview.clearCaches();
                selected = false;
                index = -1;
                return true;
            }
            ModLang.translate("editor.tool.move.selected").sendStatus(Minecraft.getInstance().player);
            selected = true;
            return true;
        }
        return false;
    }

    @Override
    public void updateTargetPos() {
        super.updateTargetPos();
        if (!selected&&selectedPos!=null) {
            for (int i = 0; i < handler.segments.size(); i++) {
                BlockPos target = handler.segments.get(i).pos;
                Vec3 eyeVec3 = Minecraft.getInstance().cameraEntity.getEyePosition();
                if (eyeVec3.distanceTo(target.getCenter()) > eyeVec3.distanceTo(selectedPos.getCenter())) {
                    continue;//当节点与摄像头之间被阻挡时
                }
                AABB aabb = new AABB(handler.segments.get(i).pos);
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
