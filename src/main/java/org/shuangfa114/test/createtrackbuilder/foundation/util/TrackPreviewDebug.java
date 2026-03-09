package org.shuangfa114.test.createtrackbuilder.foundation.util;

import com.simibubi.create.content.trains.track.TrackPlacement;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import org.shuangfa114.test.createtrackbuilder.content.item.editor.TrackEditor;
import org.shuangfa114.test.createtrackbuilder.foundation.events.ClientEvents;
import org.shuangfa114.test.createtrackbuilder.mixin.PlacementInfoAccessor;

import static org.shuangfa114.test.createtrackbuilder.foundation.util.VisualUtil.linerAdd;
import static org.shuangfa114.test.createtrackbuilder.foundation.util.VisualUtil.showText;

public class TrackPreviewDebug {

    @OnlyIn(Dist.CLIENT)
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            while (ClientEvents.KEY_MAPPING.get().consumeClick()) {
                Player player = Minecraft.getInstance().player;
                Level level = Minecraft.getInstance().level;
                ItemStack itemStack = player.getMainHandItem();
                if (itemStack.getItem() instanceof TrackEditor trackEditor && trackEditor.isPosValid(itemStack)) {
                    Segment startSegment = new Segment(trackEditor.getStartPos(itemStack), Util.getAxis(trackEditor.getStartShape(itemStack)));
                    Segment endSegment = new Segment(trackEditor.getEndPos(itemStack), Util.getAxis(trackEditor.getEndShape(itemStack)));
                    TrackPreview.preview = TrackPreview.tryConnect(level, startSegment, endSegment, true);
                    player.displayClientMessage(Component.literal(TrackPreview.preview.message == null ? "normal" : TrackPreview.preview.message), true);
                }

            }
        }

        TrackPlacement.PlacementInfo info = TrackPlacement.cached;
        PlacementInfoAccessor accessor = (PlacementInfoAccessor) info;
        if (accessor != null && accessor.getPos1() != null && accessor.getPos2() != null
                && accessor.getAxis1() != null && accessor.getAxis2() != null
                && accessor.getEnd1() != null && accessor.getEnd2() != null) {
            linerAdd(11, accessor.getPos1().getCenter(), accessor.getAxis1());
            linerAdd(12, accessor.getPos2().getCenter(), accessor.getAxis2());
            showText(accessor.getPos1(), accessor.getPos1(), 1);
            showText(accessor.getPos2(), accessor.getPos2(), 1);
            showText(accessor.getPos1(), accessor.getAxis1(), 2);
            showText(accessor.getPos2(), accessor.getAxis2(), 2);
            showText(accessor.getPos1(), accessor.getEnd1(), 3);
            showText(accessor.getPos2(), accessor.getEnd2(), 3);
        }
    }

}
