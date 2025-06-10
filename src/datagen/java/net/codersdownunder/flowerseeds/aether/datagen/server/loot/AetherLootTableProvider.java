package net.codersdownunder.flowerseeds.aether.datagen.server.loot;

import com.aetherteam.aether.block.AetherBlocks;
import net.codersdownunder.flowerseeds.aether.FlowerSeedsAether;
import net.codersdownunder.flowerseeds2.blocks.GenericFlowerCropBlock;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.WritableRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class AetherLootTableProvider extends LootTableProvider {

        public AetherLootTableProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(packOutput, Set.of(), List.of(
                    new SubProviderEntry(FlowerSeeds2Blocks::new, LootContextParamSets.BLOCK)
            ), lookupProvider);
        }

        @Override
        protected void validate(@NotNull WritableRegistry<LootTable> writableregistry, @NotNull ValidationContext validationcontext, @NotNull ProblemReporter.Collector problemreporter$collector) {
            super.validate(writableregistry, validationcontext, problemreporter$collector);
        }

        private static class FlowerSeeds2Blocks extends BlockLootSubProvider {

            protected FlowerSeeds2Blocks(HolderLookup.Provider provider) {
                super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
            }

            @Override
            protected void generate() {


                add(FlowerSeedsAether.WHITE_FLOWER_SEED.get(), (block) -> createCropDrops(FlowerSeedsAether.WHITE_FLOWER_SEED.get(), AetherBlocks.WHITE_FLOWER.asItem()));
                add(FlowerSeedsAether.PURPLE_FLOWER_SEED.get(), (block) -> createCropDrops(FlowerSeedsAether.PURPLE_FLOWER_SEED.get(), AetherBlocks.PURPLE_FLOWER.asItem()));
            }


            private LootTable.Builder createCropDrops(Block cropBlock, Item cropItem) {
                LootItemCondition.Builder cropConditionBuilder = LootItemBlockStatePropertyCondition
                        .hasBlockStateProperties(cropBlock)
                        .setProperties(StatePropertiesPredicate.Builder.properties()
                                .hasProperty(GenericFlowerCropBlock.AGE, GenericFlowerCropBlock.MAX_AGE));

                return createCropDrops(cropBlock, cropItem, cropBlock.asItem(), cropConditionBuilder);
            }



            @NotNull
            @Override
            protected Iterable<Block> getKnownBlocks() {
                return (Iterable<Block>) FlowerSeedsAether.BLOCKS.getEntries().stream().map(holder -> (Block) holder.get())::iterator;
            }
        }
    }
