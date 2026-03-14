package org.shuangfa114.test.createtrackbuilder;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.slf4j.Logger;

import static org.antlr.runtime.debug.DebugEventListener.PROTOCOL_VERSION;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CreateTrackBuilder.MODID)
public class CreateTrackBuilder {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "create_track_builder";
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(CreateTrackBuilder.MODID);
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(CreateTrackBuilder.MODID, "textures/gui/background.png"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public CreateTrackBuilder(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);
        REGISTRATE.registerEventListeners(context.getModEventBus());
        ModTab.CREATIVE_MODE_TABS.register(modEventBus);
        REGISTRATE.setCreativeTab(ModTab.TAB);
        Register.register();
        ModPackets.registerPackets();
        ModMenuTypes.register();
    }

    public static CreateRegistrate registrate() {
        return REGISTRATE;
    }
}
