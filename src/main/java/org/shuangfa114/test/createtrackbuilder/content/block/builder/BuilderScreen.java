package org.shuangfa114.test.createtrackbuilder.content.block.builder;

import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.IconButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.shuangfa114.test.createtrackbuilder.ModPackets;
import org.shuangfa114.test.createtrackbuilder.foundation.packet.builder.TrackPlacePacket;

public class BuilderScreen extends AbstractSimiContainerScreen<BuilderMenu> {
    public static final AllGuiTextures BG_BOTTOM = AllGuiTextures.SCHEMATICANNON_BOTTOM;
    public static final AllGuiTextures BG_TOP = AllGuiTextures.SCHEMATICANNON_TOP;

    protected IconButton startButton;

    public BuilderScreen(BuilderMenu container, Inventory inv, Component title) {
        super(container, inv, title);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        BuilderBlockEntity blockEntity = menu.contentHolder;
        int invX = getLeftOfCentered(AllGuiTextures.PLAYER_INVENTORY.getWidth());
        int invY = topPos + BG_TOP.getHeight() + BG_BOTTOM.getHeight() + 2;
        renderPlayerInventory(pGuiGraphics, invX, invY);

        int x = leftPos;
        int y = topPos;
    }

    @Override
    protected void init() {
        BuilderBlockEntity blockEntity = menu.contentHolder;
        setWindowSize(BG_TOP.getWidth(), BG_TOP.getHeight() + BG_BOTTOM.getHeight() + 2 + AllGuiTextures.PLAYER_INVENTORY.getHeight());
        setWindowOffset(-11, 0);
        super.init();
        int x = leftPos;
        int y = topPos;
        startButton = new IconButton(x + 130, y + 100, AllIcons.I_PLAY);
        startButton.withCallback(() -> {
            blockEntity.transmit();
            ModPackets.getChannel().sendToServer(new TrackPlacePacket(blockEntity.getBlockPos()));
        });
        addRenderableWidgets(startButton);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
    }

}
