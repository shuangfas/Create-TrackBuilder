package org.shuangfa114.test.createtrackbuilder.foundation.util;

import com.simibubi.create.content.trains.track.TrackShape;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Util {
    public static Vec3 getAxis(TrackShape shape){
        return shape.getAxes().get(0);
    }
    public static List<TrackShape> getFlatShapes(){
        return Arrays.asList(TrackShape.XO,TrackShape.ZO,TrackShape.ND,TrackShape.PD);
    }
}
