package org.shuangfa114.test.createtrackbuilder.foundation.util;

import com.simibubi.create.content.trains.track.TrackShape;
import net.minecraft.world.phys.Vec3;

public class Util {
    public static Vec3 getAxis(TrackShape shape){
        return shape.getAxes().get(0);
    }

}
