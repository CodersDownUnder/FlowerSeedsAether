package net.minecraft.world.level.block.entity;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.LockCode;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BaseContainerBlockEntity extends BlockEntity implements Container, MenuProvider, Nameable {
    private LockCode lockKey = LockCode.NO_LOCK;
    @Nullable
    private Component name;

    protected BaseContainerBlockEntity(BlockEntityType<?> p_155076_, BlockPos p_155077_, BlockState p_155078_) {
        super(p_155076_, p_155077_, p_155078_);
    }

    @Override
    public void load(CompoundTag p_155080_) {
        super.load(p_155080_);
        this.lockKey = LockCode.fromTag(p_155080_);
        if (p_155080_.contains("CustomName", 8)) {
            this.name = Component.Serializer.fromJson(p_155080_.getString("CustomName"));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag p_187461_) {
        super.saveAdditional(p_187461_);
        this.lockKey.addToTag(p_187461_);
        if (this.name != null) {
            p_187461_.putString("CustomName", Component.Serializer.toJson(this.name));
        }
    }

    public void setCustomName(Component p_58639_) {
        this.name = p_58639_;
    }

    @Override
    public Component getName() {
        return this.name != null ? this.name : this.getDefaultName();
    }

    @Override
    public Component getDisplayName() {
        return this.getName();
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return this.name;
    }

    protected abstract Component getDefaultName();

    public boolean canOpen(Player p_58645_) {
        return canUnlock(p_58645_, this.lockKey, this.getDisplayName());
    }

    public static boolean canUnlock(Player p_58630_, LockCode p_58631_, Component p_58632_) {
        if (!p_58630_.isSpectator() && !p_58631_.unlocksWith(p_58630_.getMainHandItem())) {
            p_58630_.displayClientMessage(Component.translatable("container.isLocked", p_58632_), true);
            p_58630_.playNotifySound(SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 1.0F, 1.0F);
            return false;
        } else {
            return true;
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int p_58641_, Inventory p_58642_, Player p_58643_) {
        return this.canOpen(p_58643_) ? this.createMenu(p_58641_, p_58642_) : null;
    }

    protected abstract AbstractContainerMenu createMenu(int p_58627_, Inventory p_58628_);
}
