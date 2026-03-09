package org.shuangfa114.test.createtrackbuilder.mixin;

import com.simibubi.create.content.trains.track.TrackPlacement;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = TrackPlacement.PlacementInfo.class,remap = false)
public interface PlacementInfoAccessor {
    @Accessor("end1")
    Vec3 getEnd1();
    @Accessor("end2")
    Vec3 getEnd2();
    @Accessor("normal1")
    Vec3 getNormal1();
    @Accessor("normal2")
    Vec3 getNormal2();
    @Accessor("axis1")
    Vec3 getAxis1();
    @Accessor("axis2")
    Vec3 getAxis2();
    @Accessor("pos1")
    BlockPos getPos1();
    @Accessor("pos2")
    BlockPos getPos2();
}
