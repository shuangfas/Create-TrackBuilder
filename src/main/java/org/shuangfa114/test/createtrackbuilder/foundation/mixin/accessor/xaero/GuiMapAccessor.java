package org.shuangfa114.test.createtrackbuilder.foundation.mixin.accessor.xaero;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import xaero.map.gui.GuiMap;

@Mixin(GuiMap.class)
public interface GuiMapAccessor {
    @Accessor("cameraDestination")
    void setCameraDestination(int[] destination);
}
