package com.azurewrath.interdictionpulsar;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.function.Supplier;

/**
 * 方块和物品注册器 - 集中管理模组所有方块、物品和方块实体
 */
public class BlockandItemRegistry {

    // 注册器定义
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(BuiltInRegistries.BLOCK, InterdictionPulsarMod.MOD_ID);
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(BuiltInRegistries.ITEM, InterdictionPulsarMod.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, InterdictionPulsarMod.MOD_ID);

    // 天使方块注册
    public static final Supplier<Block> ANGEL_BLOCK_BLOCK = BLOCKS.register(
            "angel_block",
            () -> new AngelBlock.AngelBlockBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.WOOD)
                            .instabreak()
                            .explosionResistance(0)
                            .noOcclusion()
            )
    );

    public static final Supplier<Item> ANGEL_BLOCK_ITEM = ITEMS.register(
            "angel_block",
            () -> new AngelBlock.AngelBlockItem(
                    ANGEL_BLOCK_BLOCK.get(),
                    new Item.Properties()
            )
    );

    // 脉冲阻拦器注册
    public static final Supplier<Block> INTERDICTION_PULSAR_BLOCK = BLOCKS.register(
            "interdiction_pulsar",
            () -> new InterdictionPulsar.InterdictionPulsarBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.METAL)
                            .strength(0.5f)
                            .sound(SoundType.METAL).pushReaction(PushReaction.BLOCK)
            )
    );

    public static final Supplier<Item> INTERDICTION_PULSAR_ITEM = ITEMS.register(
            "interdiction_pulsar",
            () -> new BlockItem(
                    INTERDICTION_PULSAR_BLOCK.get(),
                    new Item.Properties()
            )
    );

    public static final Supplier<BlockEntityType<InterdictionPulsar.InterdictionPulsarBlockEntity>>
            INTERDICTION_PULSAR_BLOCK_ENTITY = BLOCK_ENTITIES.register(
            "interdiction_pulsar",
            () -> BlockEntityType.Builder.of(
                    InterdictionPulsar.InterdictionPulsarBlockEntity::new,
                    INTERDICTION_PULSAR_BLOCK.get()
            ).build(null)
    );

    /**
     * 注册所有方块、物品和方块实体
     */
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
        BLOCK_ENTITIES.register(eventBus);
    }

    /**
     * 私有构造函数防止实例化
     */
    private BlockandItemRegistry() {
        throw new UnsupportedOperationException("这是一个工具类，不能被实例化");
    }
}