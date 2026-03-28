package org.shuangfa114.test.createtrackbuilder.api.structures;

public class SegmentEdge {
    public Segment first;
    public Segment second;

    public SegmentEdge(Segment first, Segment second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SegmentEdge edge) {
            return first.equals(edge.first) && second.equals(edge.second) || first.equals(edge.second) && second.equals(edge.first);
        }
        return false;
    }
    public static SegmentEdge of(Segment first, Segment second) {
        return new SegmentEdge(first, second);
    }

    @Override
    public int hashCode() {
        return first.hashCode() + second.hashCode();
    }
}
