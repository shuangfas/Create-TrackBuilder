package org.shuangfa114.test.createtrackbuilder.foundation.compat.xaero.element;

import net.minecraft.client.Minecraft;
import org.shuangfa114.test.createtrackbuilder.CreateTrackBuilderClient;
import org.shuangfa114.test.createtrackbuilder.api.structures.Segment;
import xaero.map.element.MapElementReader;
import xaero.map.element.render.ElementRenderLocation;
import xaero.map.gui.IRightClickableElement;
import xaero.map.gui.dropdown.rightclick.RightClickOption;

import java.util.ArrayList;

public class SegmentReader extends MapElementReader<Segment,SegmentRenderContext, SegmentRenderer> {
    @Override
    public boolean isHidden(Segment segment, SegmentRenderContext segmentRenderContext) {
        return false;
    }

    @Override
    public boolean isInteractable(ElementRenderLocation location, Segment element) {
        return true;
    }

    @Override
    public double getRenderX(Segment segment, SegmentRenderContext segmentRenderContext, float v) {
        return segment.pos.getX();
    }

    @Override
    public double getRenderZ(Segment segment, SegmentRenderContext segmentRenderContext, float v) {
        return segment.pos.getZ();
    }

    @Override
    public int getInteractionBoxLeft(Segment segment, SegmentRenderContext segmentRenderContext, float v) {
        return -getInteractionBoxRight(segment, segmentRenderContext, v);
    }

    @Override
    public int getInteractionBoxRight(Segment segment, SegmentRenderContext segmentRenderContext, float v) {
        return 14;
    }

    @Override
    public int getInteractionBoxTop(Segment segment, SegmentRenderContext segmentRenderContext, float v) {
        return -getInteractionBoxLeft(segment, segmentRenderContext, v);
    }

    @Override
    public int getInteractionBoxBottom(Segment segment, SegmentRenderContext segmentRenderContext, float v) {
        return 21;
    }

    @Override
    public int getRenderBoxLeft(Segment segment, SegmentRenderContext segmentRenderContext, float v) {
        return getInteractionBoxLeft(segment, segmentRenderContext, v);
    }

    @Override
    public int getRenderBoxRight(Segment segment, SegmentRenderContext segmentRenderContext, float v) {
        return getInteractionBoxRight(segment, segmentRenderContext, v);
    }

    @Override
    public int getRenderBoxTop(Segment segment, SegmentRenderContext segmentRenderContext, float v) {
        return getInteractionBoxTop(segment, segmentRenderContext, v);
    }

    @Override
    public int getRenderBoxBottom(Segment segment, SegmentRenderContext segmentRenderContext, float v) {
        return getInteractionBoxBottom(segment, segmentRenderContext, v);
    }

    @Override
    public int getLeftSideLength(Segment segment, Minecraft minecraft) {
        return 9;
    }

    @Override
    public String getMenuName(Segment segment) {
        return "";
    }

    @Override
    public String getFilterName(Segment segment) {
        return "";
    }

    @Override
    public int getMenuTextFillLeftPadding(Segment segment) {
        return 0;
    }

    @Override
    public int getRightClickTitleBackgroundColor(Segment segment) {
        return 0xFF_FFFFFF;
    }

    @Override
    public boolean shouldScaleBoxWithOptionalScale() {
        return true;
    }

    @Override
    public boolean isRightClickValid(Segment element) {
        return CreateTrackBuilderClient.editorHandler.segments.contains(element);
    }

    @Override
    public ArrayList<RightClickOption> getRightClickOptions(Segment element, IRightClickableElement target) {
        return super.getRightClickOptions(element, target);
    }
}
