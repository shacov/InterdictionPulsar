package com.azurewrath.interdictionpulsar;

import net.minecraft.client.gui.screens.MenuScreens;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/**
 * 客户端事件处理器 - 负责客户端的初始化和注册
 */
@EventBusSubscriber(modid = InterdictionPulsarMod.MOD_ID, value = Dist.CLIENT)
public class ClientEventHandler {

    /**
     * 客户端设置事件处理器 - 注册GUI屏幕
     */
    @SubscribeEvent
    public static void onClientSetup(RegisterMenuScreensEvent event) {
        event.register(
                MenuTypeRegistry.INTERDICTION_PULSAR_MENU.get(),
                InterdictionPulsarScreen::new
        );
    }

    /**
     * 私有构造函数防止实例化
     */
    private ClientEventHandler() {
        throw new UnsupportedOperationException("这是一个工具类，不能被实例化");
    }
}