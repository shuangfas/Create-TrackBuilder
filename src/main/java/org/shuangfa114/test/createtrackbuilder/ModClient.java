package org.shuangfa114.test.createtrackbuilder;

import org.shuangfa114.test.createtrackbuilder.api.structures.Segment;
import org.shuangfa114.test.createtrackbuilder.content.item.editor.client.EditorHandler;

import java.util.List;

public class ModClient {
    public static EditorHandler editorHandler = new EditorHandler();
    public static List<Segment> getSegments() {
        return ModClient.editorHandler.segments;
    }
}
