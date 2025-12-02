package com.azurewrath.interdictionpulsar;

import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.function.Supplier;

/**
 * 菜单类型注册器 - 管理模组所有GUI菜单的注册
 *
 * <p>负责脉冲阻拦器等GUI菜单的注册和初始化</p>
 */
public class MenuTypeRegistry {

    // 菜单注册器实例
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(BuiltInRegistries.MENU, InterdictionPulsarMod.MOD_ID);

    // 脉冲阻拦器菜单类型注册
    public static final Supplier<MenuType<InterdictionPulsarMenu>> INTERDICTION_PULSAR_MENU =
            MENUS.register("interdiction_pulsar", () ->
                    IMenuTypeExtension.create((windowId, inv, data) ->
                            new InterdictionPulsarMenu(windowId, inv, data))
            );

    /**
     * 注册所有菜单类型到事件总线
     *
     * @param eventBus 模组事件总线
     */
    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }

    /**
     * 私有构造函数防止实例化
     */
    private MenuTypeRegistry() {
        throw new UnsupportedOperationException("这是一个工具类，不能被实例化");
    }
}