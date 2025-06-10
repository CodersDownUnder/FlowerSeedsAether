package net.codersdownunder.flowerseeds.aether;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.codersdownunder.flowerseeds2.FlowerSeeds2;
import net.codersdownunder.flowerseeds2.blocks.GenericFlowerCropBlock;
import net.codersdownunder.flowerseeds2.events.VillagerTradesEventHandler;
import net.codersdownunder.flowerseeds2.util.SeedColour;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(FlowerSeedsAether.MODID)
public class FlowerSeedsAether {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "flowerseeds2aether";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "examplemod" namespace

    /*
    Flowers to add

     */
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(MODID);

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);

    public static final DeferredBlock<Block> PURPLE_FLOWER_SEED = registerBlock("purple_flower_seed",
            () -> new GenericFlowerCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).sound(SoundType.CROP), SeedColour.PURPLE));

    public static final DeferredBlock<Block> WHITE_FLOWER_SEED = registerBlock("white_flower_seed",
            () -> new GenericFlowerCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).sound(SoundType.CROP), SeedColour.WHITE));

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {

        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {

        ITEMS.register(name, () -> new ItemNameBlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }

    public FlowerSeedsAether(IEventBus modEventBus) {

        ITEMS.register(modEventBus);
        BLOCKS.register(modEventBus);

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        modEventBus.addListener(this::addCreative);

        // Register ourselves for server and other game events we are interested in
        //NeoForge.EVENT_BUS.register(this);

    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }

    @EventBusSubscriber(modid = MODID)
    public static class VillagerEventHandler {
        @SubscribeEvent
        public static void onVillagerTrades(VillagerTradesEvent pEvent) {
            if (pEvent.getType().equals(VillagerProfession.FARMER)) {
                Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = pEvent.getTrades();
                int villagerLevel = 1;

                for (DeferredHolder<Block, ? extends Block> block : BLOCKS.getEntries()) {
                    trades.get(villagerLevel).add((trader, rand) -> VillagerTradesEventHandler.addTrade(block.get()));
                }
            }
        }
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void registerItemColor(RegisterColorHandlersEvent.Item event) {
            for (DeferredHolder<Block, ? extends Block> block : BLOCKS.getEntries()) {

                GenericFlowerCropBlock item = (GenericFlowerCropBlock) block.get();
                event.register(item.getColour().get(), item.asItem());

            }
        }
    }


    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == FlowerSeeds2.MOD_TAB.getKey()) {
            for (DeferredHolder<Block, ? extends Block> block : BLOCKS.getEntries()) {
                event.accept(block.get().asItem());
            }

        }
    }

}
