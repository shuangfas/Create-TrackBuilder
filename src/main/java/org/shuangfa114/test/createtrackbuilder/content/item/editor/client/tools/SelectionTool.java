package org.shuangfa114.test.createtrackbuilder.content.item.editor.client.tools;

import com.simibubi.create.AllKeys;
import net.minecraft.client.Minecraft;
import org.shuangfa114.test.createtrackbuilder.foundation.packet.editor.SegmentIncrementalPacket;
import org.shuangfa114.test.createtrackbuilder.foundation.packet.editor.SegmentInitPacket;
import org.shuangfa114.test.createtrackbuilder.foundation.util.ModLang;
import org.shuangfa114.test.createtrackbuilder.foundation.util.Util;
import org.shuangfa114.test.createtrackbuilder.foundation.util.structures.Segment;

public class SelectionTool extends EditorToolBase {

    @Override
    public void init() {
        super.init();
        selectionRange = 20;
    }

    @Override
    public boolean handleClick(boolean left) {
        if (!left) {
            if (AllKeys.ctrlDown()) {
                if (handler.segments.size() < 2) {
                    ModLang.translate("editor.selection.not_enough").sendStatus(Minecraft.getInstance().player);
                    return false;
                }
                //如果不在客户端写nbt，物品就会刷新一下，但是懒得写了
                handler.sync(new SegmentInitPacket(handler.getActiveHotbarSlot()));
                handler.deploy();
                ModLang.translate("editor.selection.connection_init").sendStatus(Minecraft.getInstance().player);
                return true;
            }
            Segment seg = new Segment(selectedPos, Util.getBestShape(Minecraft.getInstance().player));
            if (!handler.segments.contains(seg)) {
                Minecraft.getInstance().player.displayClientMessage(ModLang.translate("editor.selection.points", handler.segments.size()).component(), true);
                handler.segments.add(seg);
                handler.sync(new SegmentIncrementalPacket(seg, handler.getActiveHotbarSlot()));
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean shouldShowSelection() {
        return true;
    }
}
