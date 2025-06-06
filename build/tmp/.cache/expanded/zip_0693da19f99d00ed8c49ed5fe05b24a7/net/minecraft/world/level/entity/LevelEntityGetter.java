package net.minecraft.world.level.entity;

import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.world.phys.AABB;

public interface LevelEntityGetter<T extends EntityAccess> {
    @Nullable
    T get(int p_156931_);

    @Nullable
    T get(UUID p_156939_);

    Iterable<T> getAll();

    <U extends T> void get(EntityTypeTest<T, U> p_156935_, AbortableIterationConsumer<U> p_261602_);

    void get(AABB p_156937_, Consumer<T> p_156938_);

    <U extends T> void get(EntityTypeTest<T, U> p_156932_, AABB p_156933_, AbortableIterationConsumer<U> p_261542_);
}
