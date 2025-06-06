package net.minecraft.world.level.storage.loot;

import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.DataResult.PartialResult;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import org.slf4j.Logger;


//PATCH 1.20.2: This whole class has to be revalidated. It previously used a very uggly patch with a hack.
//This might need to be refactored into a codec. But currently this is reverted to a clean state.
public class LootDataType<T> {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final LootDataType<LootItemCondition> PREDICATE = new LootDataType<>(LootItemConditions.CODEC, "predicates", createSimpleValidator());
    public static final LootDataType<LootItemFunction> MODIFIER = new LootDataType<>(LootItemFunctions.CODEC, "item_modifiers", createSimpleValidator());
    public static final LootDataType<LootTable> TABLE = new LootDataType<>(LootTable.CODEC, "loot_tables", createLootTableValidator(), LootTable.EMPTY, LootTable::setLootTableId);
    private final Codec<T> codec;
    private final String directory;
    private final LootDataType.Validator<T> validator;
    @org.jetbrains.annotations.Nullable
    private final T defaultValue;
    private final Codec<Optional<T>> conditionalCodec;
    private final java.util.function.BiConsumer<T, ResourceLocation> idSetter;

    /**
     * @deprecated Neo: use the constructor {@link #LootDataType(Codec, String, Validator, T, java.util.function.BiConsumer) with a default value and id setter} to support conditions
     */
    @Deprecated
    private LootDataType(Codec<T> p_298773_, String p_279433_, LootDataType.Validator<T> p_279363_) {
        this(p_298773_, p_279433_, p_279363_, null, (it, id) -> {});
    }
    private LootDataType(Codec<T> p_298773_, String p_279433_, LootDataType.Validator<T> p_279363_, @org.jetbrains.annotations.Nullable T defaultValue, java.util.function.BiConsumer<T, ResourceLocation> idSetter) {
        this.codec = p_298773_;
        this.directory = p_279433_;
        this.validator = p_279363_;
        this.defaultValue = defaultValue;
        this.idSetter = idSetter;
        this.conditionalCodec = net.neoforged.neoforge.common.conditions.ConditionalOps.createConditionalCodec(codec);
    }

    public String directory() {
        return this.directory;
    }

    public void runValidation(ValidationContext p_279366_, LootDataId<T> p_279106_, T p_279124_) {
        this.validator.run(p_279366_, p_279106_, p_279124_);
    }

    public Optional<T> deserialize(ResourceLocation p_279253_, JsonElement p_279330_) {
        return deserializeOrDefault(p_279253_, JsonOps.INSTANCE, p_279330_);
    }

    public <C> Optional<T> deserializeOrDefault(ResourceLocation location, com.mojang.serialization.DynamicOps<C> ops, C object) {
        var dataresult = this.conditionalCodec.parse(ops, object);
        dataresult.error().ifPresent(p_297991_ -> LOGGER.error("Couldn't parse element {}:{} - {}", this.directory, location, p_297991_.message()));
        return dataresult.result().map(it -> {
            it.ifPresent(val -> idSetter.accept(val, location));
            return it.orElse(defaultValue);
        });
    }

    public static Stream<LootDataType<?>> values() {
        return Stream.of(PREDICATE, MODIFIER, TABLE);
    }

    private static <T extends LootContextUser> LootDataType.Validator<T> createSimpleValidator() {
        return (p_279353_, p_279374_, p_279097_) -> p_279097_.validate(
                p_279353_.enterElement("{" + p_279374_.type().directory + ":" + p_279374_.location() + "}", p_279374_)
            );
    }

    private static LootDataType.Validator<LootTable> createLootTableValidator() {
        return (p_279333_, p_279227_, p_279406_) -> {
            p_279406_ = net.neoforged.neoforge.event.EventHooks.loadLootTable(p_279406_.getLootTableId(), p_279406_);
            p_279406_.validate(
                    p_279333_.setParams(p_279406_.getParamSet()).enterElement("{" + p_279227_.type().directory + ":" + p_279227_.location() + "}", p_279227_)
            );
        };
    }

    @FunctionalInterface
    public interface Validator<T> {
        void run(ValidationContext p_279419_, LootDataId<T> p_279145_, T p_279326_);
    }
}
