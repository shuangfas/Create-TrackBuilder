package org.shuangfa114.test.createtrackbuilder.foundation.compat.xaero.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import org.shuangfa114.test.createtrackbuilder.foundation.mixin.accessor.xaero.GuiMapAccessor;
import xaero.map.element.MapElementReader;
import xaero.map.element.render.ElementRenderLocation;
import xaero.map.gui.IRightClickableElement;
import xaero.map.gui.dropdown.rightclick.RightClickOption;

import java.util.ArrayList;

public class SegmentReader extends MapElementReader<SegmentIterator, SegmentRenderContext, SegmentRenderer> {
    @Override
    public boolean isHidden(SegmentIterator iterator, SegmentRenderContext segmentRenderContext) {
        return false;
    }

    @Override
    public boolean isInteractable(ElementRenderLocation location, SegmentIterator element) {
        return true;
    }

    @Override
    public double getRenderX(SegmentIterator iterator, SegmentRenderContext segmentRenderContext, float v) {
        return iterator.current().pos.getX() + 0.25;
    }

    @Override
    public double getRenderZ(SegmentIterator iterator, SegmentRenderContext segmentRenderContext, float v) {
        return iterator.current().pos.getZ() + 0.25;
    }

    @Override
    public int getInteractionBoxLeft(SegmentIterator iterator, SegmentRenderContext segmentRenderContext, float v) {
        return -getInteractionBoxRight(iterator, segmentRenderContext, v);
    }

    @Override
    public int getInteractionBoxRight(SegmentIterator iterator, SegmentRenderContext segmentRenderContext, float v) {
        return 14;
    }

    @Override
    public int getInteractionBoxTop(SegmentIterator iterator, SegmentRenderContext segmentRenderContext, float v) {
        return -getInteractionBoxBottom(iterator, segmentRenderContext, v);
    }

    @Override
    public int getInteractionBoxBottom(SegmentIterator iterator, SegmentRenderContext segmentRenderContext, float v) {
        return 21;
    }

    @Override
    public int getRenderBoxLeft(SegmentIterator iterator, SegmentRenderContext segmentRenderContext, float v) {
        return getInteractionBoxLeft(iterator, segmentRenderContext, v);
    }

    @Override
    public int getRenderBoxRight(SegmentIterator iterator, SegmentRenderContext segmentRenderContext, float v) {
        return getInteractionBoxRight(iterator, segmentRenderContext, v);
    }

    @Override
    public int getRenderBoxTop(SegmentIterator iterator, SegmentRenderContext segmentRenderContext, float v) {
        return getInteractionBoxTop(iterator, segmentRenderContext, v);
    }

    @Override
    public int getRenderBoxBottom(SegmentIterator iterator, SegmentRenderContext segmentRenderContext, float v) {
        return getInteractionBoxBottom(iterator, segmentRenderContext, v);
    }

    @Override
    public int getLeftSideLength(SegmentIterator iterator, Minecraft minecraft) {
        return 9;
    }

    @Override
    public String getMenuName(SegmentIterator iterator) {
        return "";
    }

    @Override
    public String getFilterName(SegmentIterator iterator) {
        return "";
    }

    @Override
    public int getMenuTextFillLeftPadding(SegmentIterator iterator) {
        return 0;
    }

    @Override
    public int getRightClickTitleBackgroundColor(SegmentIterator iterator) {
        return 0xFF_FFFFFF;
    }

    @Override
    public boolean shouldScaleBoxWithOptionalScale() {
        return true;
    }

    @Override
    public boolean isRightClickValid(SegmentIterator element) {
        return true;
    }

    @Override
    public ArrayList<RightClickOption> getRightClickOptions(SegmentIterator element, IRightClickableElement target) {
        ArrayList<RightClickOption> rightClickOptions = new ArrayList();
        BlockPos current = element.current().pos;
        rightClickOptions.add(new RightClickOption(String.format("X: %d, Y: %s, Z: %d", current.getX(), current.getY(), current.getZ()), 0, target) {
            @Override
            public void onAction(Screen screen) {

            }
        });
        rightClickOptions.add(new RightClickOption("To Next Segment", 1, target) {
            public void onAction(Screen screen) {
                ((GuiMapAccessor) screen).setCameraDestination(new int[]{element.peek().pos.getX(), element.peek().pos.getZ()});
            }
            @Override
            public boolean isActive() {
                return element.hasNext();
            }
        });
        return rightClickOptions;
    }
}
