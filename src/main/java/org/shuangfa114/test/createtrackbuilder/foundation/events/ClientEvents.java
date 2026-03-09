package org.shuangfa114.test.createtrackbuilder.foundation.events;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.schematics.client.SchematicHandler;
import net.createmod.catnip.render.DefaultSuperRenderTypeBuffer;
import net.createmod.catnip.render.SuperRenderTypeBuffer;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import org.shuangfa114.test.createtrackbuilder.CreateTrackBuilder;
import org.shuangfa114.test.createtrackbuilder.CreateTrackBuilderClient;
import org.shuangfa114.test.createtrackbuilder.foundation.blockEntity.behaviour.ItemPlacingRender;
import org.shuangfa114.test.createtrackbuilder.foundation.util.TrackPreview;
import org.shuangfa114.test.createtrackbuilder.foundation.util.TrackPreviewDebug;

@Mod.EventBusSubscriber(modid = CreateTrackBuilder.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEvents {
    public static final Lazy<KeyMapping> KEY_MAPPING = Lazy.of(() -> new KeyMapping(
            "key.examplemod.example2",
            KeyConflictContext.IN_GAME, // Mapping can only be used when a screen is open
            InputConstants.Type.KEYSYM, // Default mapping is on the mouse
            GLFW.GLFW_KEY_END, // Default mouse input is the left mouse button
            "key.categories.examplemod.examplecategory" // Mapping will be in the new example category
    ));

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().level == null) {
            return;
        }
        if(event.phase == TickEvent.Phase.START) {
            return;
        }
        TrackPreviewDebug.onClientTick(event);
        //ItemPreview.onClientTick(event);
        ItemPlacingRender.tick();
        TrackPreview.clientTick();
        CreateTrackBuilderClient.editorHandler.tick();
    }

    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent event) {
        event.register(KEY_MAPPING.get());
    }

    @SubscribeEvent
    public static void onRenderWorld(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES)
            return;

        PoseStack ms = event.getPoseStack();
        ms.pushPose();
        SuperRenderTypeBuffer buffer = DefaultSuperRenderTypeBuffer.getInstance();
        Vec3 camera = Minecraft.getInstance().gameRenderer.getMainCamera()
                .getPosition();
        CreateTrackBuilderClient.editorHandler.render(ms, buffer, camera);
        buffer.draw();
        RenderSystem.enableCull();
        ms.popPose();
    }

    @SubscribeEvent
    public static void onOverlayRender(RenderGuiOverlayEvent event) {
        if(event.getOverlay().overlay() instanceof SchematicHandler){//指定一个overlay，不然会重复渲染
            Window window = event.getWindow();
            CreateTrackBuilderClient.editorHandler.renderGui(event.getGuiGraphics(), event.getPartialTick()
                    , window.getGuiScaledWidth()
                    , window.getGuiScaledHeight());
        }
    }
}
