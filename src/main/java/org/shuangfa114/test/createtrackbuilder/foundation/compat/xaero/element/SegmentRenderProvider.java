package org.shuangfa114.test.createtrackbuilder.foundation.compat.xaero.element;

import org.shuangfa114.test.createtrackbuilder.ModClient;
import xaero.map.element.MapElementRenderProvider;
import xaero.map.element.render.ElementRenderLocation;

public class SegmentRenderProvider extends MapElementRenderProvider<SegmentIterator, SegmentRenderContext> {
    private SegmentIterator iterator;

    @Override
    public void begin(ElementRenderLocation location, SegmentRenderContext context) {
        iterator = new SegmentIterator(ModClient.getSegments());
    }

    @Override
    public boolean hasNext(ElementRenderLocation location, SegmentRenderContext context) {
        return iterator.hasNext();
    }

    @Override
    public SegmentIterator getNext(ElementRenderLocation location, SegmentRenderContext context) {
        iterator.next();
        return iterator;
    }

    @Override
    public SegmentIterator setupContextAndGetNext(ElementRenderLocation location, SegmentRenderContext context) {
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
    public SegmentIterator getNext(int i, SegmentRenderContext segmentRenderContext) {
        return null;
    }
}
