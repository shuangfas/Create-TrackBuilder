package org.shuangfa114.test.createtrackbuilder.foundation.events;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.shuangfa114.test.createtrackbuilder.CreateTrackBuilder;
import org.shuangfa114.test.createtrackbuilder.CreateTrackBuilderClient;

@Mod.EventBusSubscriber(modid = CreateTrackBuilder.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class InputEvents {
    @SubscribeEvent
    public static void onMouseInput(InputEvent.MouseButton.Pre event){
        if (Minecraft.getInstance().screen != null)
            return;
        int button = event.getButton();
        boolean pressed = !(event.getAction() == 0);
        if (CreateTrackBuilderClient.editorHandler.onMouseInput(button, pressed))
            event.setCanceled(true);
    }
    @SubscribeEvent
    public static void onMouseScrolled(InputEvent.MouseScrollingEvent event){
        if (Minecraft.getInstance().screen != null)
            return;

        double delta = event.getScrollDelta();
        if(CreateTrackBuilderClient.editorHandler.mouseScrolled(delta)){
            event.setCanceled(true);
        }
    }
    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (Minecraft.getInstance().screen != null)
            return;

        int key = event.getKey();
        boolean pressed = !(event.getAction() == 0);

        CreateTrackBuilderClient.editorHandler.onKeyInput(key, pressed);
    }
}
