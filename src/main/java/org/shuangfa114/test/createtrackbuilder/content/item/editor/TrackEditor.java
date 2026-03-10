package org.shuangfa114.test.createtrackbuilder.content.item.editor;

import com.simibubi.create.content.trains.track.TrackShape;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;

public class TrackEditor extends Item {

    public TrackEditor(Properties pProperties) {
        super(pProperties);
    }

    public TrackShape getBestShape(Player player) {
        if (player != null) {
            Vec3 lookAngle = player.getLookAngle();
            lookAngle = lookAngle.multiply(1, 0, 1);
            if (Mth.equal(lookAngle.length(), 0))
                lookAngle = VecHelper.rotate(new Vec3(0, 0, 1), -player.getYRot(), Direction.Axis.Y);
            lookAngle = lookAngle.normalize();
            TrackShape best = TrackShape.ZO;
            double bestValue = Float.MAX_VALUE;
            for (TrackShape shape : TrackShape.values()) {
                if (shape.isJunction() || shape.isPortal())
                    continue;
                Vec3 axis = shape.getAxes()
                        .get(0);
                double distance = Math.min(axis.distanceToSqr(lookAngle), axis.normalize()
                        .scale(-1)
                        .distanceToSqr(lookAngle));
                if (distance > bestValue)
                    continue;
                bestValue = distance;
                best = shape;
            }
            return best;
        }
        return null;
    }

}
