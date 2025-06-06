package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

public class PlayerInteractTrigger extends SimpleCriterionTrigger<PlayerInteractTrigger.TriggerInstance> {
    @Override
    public Codec<PlayerInteractTrigger.TriggerInstance> codec() {
        return PlayerInteractTrigger.TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer p_61495_, ItemStack p_61496_, Entity p_61497_) {
        LootContext lootcontext = EntityPredicate.createContext(p_61495_, p_61497_);
        this.trigger(p_61495_, p_61501_ -> p_61501_.matches(p_61496_, lootcontext));
    }

    public static record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ItemPredicate> item, Optional<ContextAwarePredicate> entity)
        implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<PlayerInteractTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
            p_311434_ -> p_311434_.group(
                        ExtraCodecs.strictOptionalField(EntityPredicate.ADVANCEMENT_CODEC, "player").forGetter(PlayerInteractTrigger.TriggerInstance::player),
                        ExtraCodecs.strictOptionalField(ItemPredicate.CODEC, "item").forGetter(PlayerInteractTrigger.TriggerInstance::item),
                        ExtraCodecs.strictOptionalField(EntityPredicate.ADVANCEMENT_CODEC, "entity").forGetter(PlayerInteractTrigger.TriggerInstance::entity)
                    )
                    .apply(p_311434_, PlayerInteractTrigger.TriggerInstance::new)
        );

        public static Criterion<PlayerInteractTrigger.TriggerInstance> itemUsedOnEntity(
            Optional<ContextAwarePredicate> p_299056_, ItemPredicate.Builder p_286289_, Optional<ContextAwarePredicate> p_301218_
        ) {
            return CriteriaTriggers.PLAYER_INTERACTED_WITH_ENTITY
                .createCriterion(new PlayerInteractTrigger.TriggerInstance(p_299056_, Optional.of(p_286289_.build()), p_301218_));
        }

        public static Criterion<PlayerInteractTrigger.TriggerInstance> itemUsedOnEntity(
            ItemPredicate.Builder p_286235_, Optional<ContextAwarePredicate> p_298893_
        ) {
            return itemUsedOnEntity(Optional.empty(), p_286235_, p_298893_);
        }

        public boolean matches(ItemStack p_61522_, LootContext p_61523_) {
            if (this.item.isPresent() && !this.item.get().matches(p_61522_)) {
                return false;
            } else {
                return this.entity.isEmpty() || this.entity.get().matches(p_61523_);
            }
        }

        @Override
        public void validate(CriterionValidator p_312461_) {
            SimpleCriterionTrigger.SimpleInstance.super.validate(p_312461_);
            p_312461_.validateEntity(this.entity, ".entity");
        }
    }
}
