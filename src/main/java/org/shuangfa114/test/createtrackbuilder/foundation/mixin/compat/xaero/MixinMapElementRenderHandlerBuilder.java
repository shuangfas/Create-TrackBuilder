package org.shuangfa114.test.createtrackbuilder.foundation.mixin.compat.xaero;

import com.llamalad7.mixinextras.sugar.Local;
import org.shuangfa114.test.createtrackbuilder.foundation.compat.xaero.element.SegmentRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xaero.map.element.MapElementRenderHandler;
import xaero.map.element.render.ElementRenderer;

import java.util.List;

@Mixin(value = MapElementRenderHandler.Builder.class,remap = false)
public class MixinMapElementRenderHandlerBuilder {
    @Inject(method = "build",at = @At("RETURN"))
    public void addSegmentRenderer(CallbackInfoReturnable<MapElementRenderHandler> cir, @Local List<ElementRenderer<?, ?, ?>> renderers) {
        renderers.add(SegmentRenderer.create());
    }
}
