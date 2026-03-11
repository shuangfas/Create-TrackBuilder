package org.shuangfa114.test.createtrackbuilder.content.item.editor.client.tools;

import com.simibubi.create.AllKeys;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.shuangfa114.test.createtrackbuilder.content.item.editor.packet.SegmentIncrementalPacket;
import org.shuangfa114.test.createtrackbuilder.content.item.editor.packet.SegmentInitPacket;
import org.shuangfa114.test.createtrackbuilder.foundation.util.ModLang;
import org.shuangfa114.test.createtrackbuilder.foundation.util.algorithm.Segment;

public class SelectionTool extends EditorToolBase {
    public static boolean enableAxis = false;

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
                    ModLang.translate("selection.not_enough").sendStatus(Minecraft.getInstance().player);
                    return false;
                }
                //如果不在客户端写nbt，物品就会刷新一下，但是懒得写了
                handler.sync(new SegmentInitPacket(handler.getActiveHotbarSlot()));
                handler.deploy();
                ModLang.translate("selection.connection_init").sendStatus(Minecraft.getInstance().player);
                return true;
            }
            Segment seg = new Segment(selectedPos);
            if (!handler.segments.contains(seg)) {
                Minecraft.getInstance().player.displayClientMessage(ModLang.translateDirect("selection.points", handler.segments.size()), true);
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
