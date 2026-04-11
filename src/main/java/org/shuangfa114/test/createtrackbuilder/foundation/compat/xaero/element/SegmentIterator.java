package org.shuangfa114.test.createtrackbuilder.foundation.compat.xaero.element;

import org.shuangfa114.test.createtrackbuilder.api.structures.Segment;

import java.util.List;
import java.util.ListIterator;

public class SegmentIterator implements ListIterator<Segment>, Cloneable {
    private final List<Segment> segments;
    private int index;

    public SegmentIterator(List<Segment> segments) {
        this.index = -1;
        this.segments = segments;
    }

    private SegmentIterator(int index, List<Segment> segments) {
        this.index = index;
        this.segments = segments;
    }

    @Override
    public boolean hasNext() {
        return nextIndex() < segments.size();
    }

    @Override
    public Segment next() {
        return segments.get(++index);
    }

    @Override
    public boolean hasPrevious() {
        return index > 0;
    }

    @Override
    public Segment previous() {
        return segments.get(--index);
    }

    @Override
    public int nextIndex() {
        return index + 1;
    }

    @Override
    public int previousIndex() {
        return index - 1;
    }

    public boolean hasCurrent() {
        return index > -1;
    }

    public Segment current() {
        return segments.get(index);
    }

    public int currentIndex() {
        return index;
    }

    public Segment peek() {
        return segments.get(index+1);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove");
    }

    @Override
    public void set(Segment segment) {
        segments.set(index, segment);
    }

    @Override
    public void add(Segment segment) {
        segments.add(index, segment);
    }

    @Override
    public SegmentIterator clone() throws CloneNotSupportedException {
        return (SegmentIterator) super.clone();
    }
}
