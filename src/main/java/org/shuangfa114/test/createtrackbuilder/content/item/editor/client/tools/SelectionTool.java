package org.shuangfa114.test.createtrackbuilder.content.item.editor.client.tools;

import net.minecraft.client.Minecraft;
import org.shuangfa114.test.createtrackbuilder.foundation.util.ModLang;
import org.shuangfa114.test.createtrackbuilder.foundation.util.Util;
import org.shuangfa114.test.createtrackbuilder.api.structures.Segment;

public class SelectionTool extends EditorToolBase {

    @Override
    public void init() {
        super.init();
        selectionRange = 20;
    }

    @Override
    public boolean handleClick(boolean left) {
        if (!left) {
            Segment seg = new Segment(selectedPos, Util.getBestShape(Minecraft.getInstance().player));
            if (!handler.segments.contains(seg)) {
                Minecraft.getInstance().player.displayClientMessage(ModLang.translate("editor.selection.points", handler.segments.size()).component(), true);
                handler.addSegment(seg);
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
