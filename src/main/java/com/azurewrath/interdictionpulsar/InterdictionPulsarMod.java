package com.azurewrath.interdictionpulsar;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * 模组主类 - Minecraft NeoForge模组的入口点
 *
 * <p>负责模组的初始化、组件注册和网络通信设置</p>
 */
@Mod(InterdictionPulsarMod.MOD_ID)
public class InterdictionPulsarMod {

    /** 模组ID常量 */
    public static final String MOD_ID = "interdictionpulsar";

    /**
     * 模组构造函数
     *
     * @param eventBus 模组事件总线
     */
    public InterdictionPulsarMod(IEventBus eventBus) {
        registerComponents(eventBus);
        eventBus.addListener(this::registerNetworkPackets);
        eventBus.addListener(this::addItemsToCreativeTabs);
    }

    /**
     * 注册所有模组组件
     *
     * @param eventBus 模组事件总线
     */
    private void registerComponents(IEventBus eventBus) {
        BlockandItemRegistry.register(eventBus);
        MenuTypeRegistry.register(eventBus);
    }

    /**
     * 将模组物品添加到创意模式标签页
     *
     * @param event 创意标签页构建事件
     */
    private void addItemsToCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(BlockandItemRegistry.ANGEL_BLOCK_ITEM.get());
            event.accept(BlockandItemRegistry.INTERDICTION_PULSAR_ITEM.get());
        }
    }

    /**
     * 注册网络数据包
     */
    private void registerNetworkPackets(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(MOD_ID);
        registrar.playToServer(
                PulsarConfigPacket.TYPE,
                PulsarConfigPacket.STREAM_CODEC,
                PulsarConfigPacket::handle
        );
    }

    /**
     * 创建资源定位器
     *
     * @param path 资源路径
     * @return 资源定位器实例
     */
    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}