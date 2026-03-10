package org.shuangfa114.test.createtrackbuilder.content.item.editor.client;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllKeys;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.createmod.catnip.render.SuperRenderTypeBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.shuangfa114.test.createtrackbuilder.ModPackets;
import org.shuangfa114.test.createtrackbuilder.content.item.editor.TrackEditor;
import org.shuangfa114.test.createtrackbuilder.content.item.editor.client.tools.EditorToolType;
import org.shuangfa114.test.createtrackbuilder.foundation.util.algorithm.Segment;

import java.util.ArrayList;
import java.util.List;

//I'm king of copying and pasting!!!!
public class EditorHandler {
    private static final int SYNC_DELAY = 10;
    public List<Segment> segments;
    private ToolSelectionScreen selectionScreen;
    private boolean initialized;
    private boolean active;
    private EditorToolType currentTool;
    private int activeHotbarSlot;
    private ItemStack activeEditorItem;

    public EditorHandler() {
        currentTool = EditorToolType.SELECTION_INIT;
        selectionScreen = new ToolSelectionScreen(ImmutableList.of(EditorToolType.SELECTION_INIT), this::equip);
        segments = new ArrayList<>();
    }

    public void tick() {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        ItemStack stack = findEditorInHand(player);
        if (stack == null) {
            active = false;
            activeHotbarSlot = 0;
            activeEditorItem = null;
            return;
        }
        if (!active) {
            init(player, stack);
        }
        selectionScreen.update();
        currentTool.getTool().updateSelection();
        currentTool.getTool().tick();
    }

    public void render(PoseStack ms, SuperRenderTypeBuffer buffer, Vec3 camera) {
        if (active) {
            ms.pushPose();
            currentTool.getTool().renderTool(ms, buffer, camera);
            //ms.last().pose().setTranslation();
            ms.popPose();
        }
    }

    public void renderGui(GuiGraphics graphics, float partialTicks, int width, int height) {
        if (Minecraft.getInstance().options.hideGui || !active) {
            return;
        }
//        if (activeEditorItem != null){
//            int x = mainWindow.getGuiScaledWidth() / 2 - 88;
//            int y = mainWindow.getGuiScaledHeight() - 19;
//            RenderSystem.enableDepthTest();
//            AllGuiTextures.SCHEMATIC_SLOT.render(graphics, x + 20 * slot, y);
//        }
        selectionScreen.renderPassive(graphics, partialTicks);
    }

    public void init(LocalPlayer player, ItemStack stack) {
        active = true;
        load(stack);
        if (initialized) {
            EditorToolType toolBefore = currentTool;
            selectionScreen = new ToolSelectionScreen(EditorToolType.getTools(player.isCreative()), this::equip);
            if (toolBefore != null&&EditorToolType.getTools(player.isCreative()).contains(toolBefore)) {
                selectionScreen.setSelectedElement(toolBefore);
                equip(toolBefore);
            }
        } else
            selectionScreen = new ToolSelectionScreen(EditorToolType.getToolsBeforeInit(), this::equip);
    }

    public boolean onMouseInput(int button, boolean pressed) {
        if (!active)
            return false;
        if (!pressed)
            return false;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player.isShiftKeyDown())
            return false;
        return currentTool.getTool().handleClick(button == 0);
    }

    public void onKeyInput(int key, boolean pressed) {
        if (!active)
            return;
        if (!AllKeys.TOOL_MENU.doesModifierAndCodeMatch(key))
            return;

        if (pressed && !selectionScreen.focused)
            selectionScreen.focused = true;
        if (!pressed && selectionScreen.focused) {
            selectionScreen.focused = false;
            selectionScreen.onClose();
        }
    }

    public boolean mouseScrolled(double delta) {
        if (!active)
            return false;

        if (selectionScreen.focused) {
            selectionScreen.cycle((int) Math.signum(delta));
            return true;
        }
        if (AllKeys.ctrlDown())
            return currentTool.getTool()
                    .handleMouseWheel(delta);
        return false;
    }

    public void sync(SimplePacketBase packet) {
        if (this.getActiveEditorItem() == null)
            return;
        ModPackets.getChannel().sendToServer(packet);
    }

    public void equip(EditorToolType tool) {
        this.currentTool = tool;
        currentTool.getTool()
                .init();
    }

    public void deploy() {
        if (!initialized) {
            selectionScreen = new ToolSelectionScreen(EditorToolType.getTools(Minecraft.getInstance().player.isCreative()), this::equip);
        }
        initialized = true;
    }

    private ItemStack findEditorInHand(Player player) {
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof TrackEditor))
            return null;

        activeEditorItem = stack;
        activeHotbarSlot = player.getInventory().selected;
        return stack;
    }

    public ItemStack getActiveEditorItem() {
        return activeEditorItem;
    }

    public int getActiveHotbarSlot() {
        return activeHotbarSlot;
    }
    public void load(ItemStack stack) {
        if (segments.isEmpty()) {
            segments = Segment.tagToList(stack.getOrCreateTag());
        }
        initialized = stack.getOrCreateTag().getBoolean("Initialized");
    }
}
