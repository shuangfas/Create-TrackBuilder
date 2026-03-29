package org.shuangfa114.test.createtrackbuilder.foundation.compat.xaero.element;

import org.shuangfa114.test.createtrackbuilder.CreateTrackBuilderClient;
import org.shuangfa114.test.createtrackbuilder.api.structures.Segment;
import xaero.map.element.MapElementRenderProvider;
import xaero.map.element.render.ElementRenderLocation;

import java.util.Iterator;

public class SegmentRenderProvider extends MapElementRenderProvider<Segment, SegmentRenderContext> {
    private Iterator<Segment> iterator;

    @Override
    public void begin(ElementRenderLocation location, SegmentRenderContext context) {
        iterator = CreateTrackBuilderClient.editorHandler.segments.iterator();
    }

    @Override
    public boolean hasNext(ElementRenderLocation location, SegmentRenderContext context) {
        return iterator.hasNext();
    }

    @Override
    public Segment getNext(ElementRenderLocation location, SegmentRenderContext context) {
        return iterator.next();
    }

    @Override
    public Segment setupContextAndGetNext(ElementRenderLocation location, SegmentRenderContext context) {
        return getNext(location, context);
    }
    //————————————————————
    @Override
    public void end(int i, SegmentRenderContext segmentRenderContext) {

    }

    @Override
    public void begin(int i, SegmentRenderContext segmentRenderContext) {

    }

    @Override
    public boolean hasNext(int i, SegmentRenderContext segmentRenderContext) {
        return false;
    }

    @Override
    public Segment getNext(int i, SegmentRenderContext segmentRenderContext) {
        return null;
    }
}
