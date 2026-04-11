package org.shuangfa114.test.createtrackbuilder.foundation.mixin.compat.xaero;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.shuangfa114.test.createtrackbuilder.foundation.compat.xaero.element.SegmentIterator;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import xaero.map.element.MapElementRenderHandler;

@Debug(export = true)
@Mixin(value = MapElementRenderHandler.class, remap = false)
public class MixinMapElementRenderHandler {
    @Shadow
    private Object workingHovered;

    @Definition(id = "workingHovered", field = "Lxaero/map/element/MapElementRenderHandler;workingHovered:Ljava/lang/Object;")
    @Expression("this.workingHovered = ?")
    @WrapOperation(method = "transformAndRenderElement", at = @At("MIXINEXTRAS:EXPRESSION"))
    private void test(MapElementRenderHandler instance, Object value, Operation<Void> original) throws CloneNotSupportedException {
        if (value instanceof SegmentIterator segmentIterator) {
            workingHovered = segmentIterator.clone();
        } else {
            original.call(instance, value);
        }
    }
}
