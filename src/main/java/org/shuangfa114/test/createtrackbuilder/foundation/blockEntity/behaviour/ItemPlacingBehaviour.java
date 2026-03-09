package org.shuangfa114.test.createtrackbuilder.foundation.blockEntity.behaviour;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.*;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.shuangfa114.test.createtrackbuilder.content.item.editor.TrackEditor;

import java.util.function.Predicate;

//todo MultiFilterBehaviour
public class ItemPlacingBehaviour extends BlockEntityBehaviour implements ValueSettingsBehaviour {
    public static final BehaviourType<ItemPlacingBehaviour> TYPE = new BehaviourType<>();
    public ItemStack template = ItemStack.EMPTY;
    public ValueBoxTransform slotPositioning;
    public Predicate<ItemStack> predicate;

    public ItemPlacingBehaviour(SmartBlockEntity be, ValueBoxTransform slot) {
        super(be);
        slotPositioning = slot;
    }

    public ItemPlacingBehaviour withPredicate(Predicate<ItemStack> predicate) {
        this.predicate = predicate;
        return this;
    }

    @Override
    public void write(CompoundTag nbt, boolean clientPacket) {
        nbt.put("template", template.serializeNBT());
    }

    @Override
    public void read(CompoundTag nbt, boolean clientPacket) {
        template = ItemStack.of(nbt.getCompound("template"));
    }

    @Override
    public boolean testHit(Vec3 hit) {
        BlockState state = blockEntity.getBlockState();
        Vec3 localHit = hit.subtract(Vec3.atLowerCornerOf(blockEntity.getBlockPos()));
        return slotPositioning.testHit(getWorld(), getPos(), state, localHit);
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public ValueBoxTransform getSlotPositioning() {
        return slotPositioning;
    }

    @Override
    public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
        return new ValueSettingsBoard(Component.literal("test"), 1, 1, null, null);
    }

    @Override
    public void setValueSettings(Player player, ValueSettings valueSetting, boolean ctrlDown) {

    }

    public Component getLabel() {
        return Component.literal("ItemPlacingBehaviour");
    }

    @Override
    public boolean acceptsValueSettings() {
        return false;
    }

    @Override
    public ValueSettings getValueSettings() {
        return null;
    }

    @Override
    public void onShortInteract(Player player, InteractionHand hand, Direction side, BlockHitResult hitResult) {
        ItemStack itemInHand = player.getItemInHand(hand);
        ItemStack toApply = itemInHand.copy();
        if (!player.isCreative() || ItemHelper
                .extract(new InvWrapper(player.getInventory()),
                        stack -> ItemHandlerHelper.canItemStacksStack(stack, template), true)
                .isEmpty())
            player.getInventory().placeItemBackInInventory(template.copy());
        if (!setTemplate(toApply)) {
            player.displayClientMessage(CreateLang.translateDirect("logistics.filter.invalid_item"), true);
            AllSoundEvents.DENY.playOnServer(player.level(), player.blockPosition(), 1, 1);
            return;
        }
        if (!player.isCreative()) {
            if (predicateTest(itemInHand)) {
                if (itemInHand.getCount() == 1)
                    player.setItemInHand(hand, ItemStack.EMPTY);
                else
                    itemInHand.shrink(1);
            }
        }
    }

    public boolean predicateTest(ItemStack itemInHand) {
        return predicate.test(itemInHand) || itemInHand.isEmpty();
    }

    public boolean setTemplate(ItemStack stack) {
        if (predicateTest(stack)) {
            template = stack;
            blockEntity.setChanged();
            blockEntity.sendData();
            return true;
        }
        return false;
    }

    @Override
    public void destroy() {
        if (template.getItem() instanceof TrackEditor) {
            Vec3 pos = VecHelper.getCenterOf(getPos());
            Level world = getWorld();
            world.addFreshEntity(new ItemEntity(world, pos.x, pos.y, pos.z, template.copy()));
        }
        super.destroy();
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }

    public float getRenderDistance() {
        return AllConfigs.client().filterItemRenderDistance.getF();
    }
}
