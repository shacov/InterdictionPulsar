package com.azurewrath.interdictionpulsar;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;

/**
 * 脉冲阻拦器配置菜单 - 管理GUI后端逻辑和数据同步
 */
public class InterdictionPulsarMenu extends AbstractContainerMenu {

    // 配置字段索引常量
    public static final int FIELD_TICK_INTERVAL = InterdictionPulsar.FieldIndices.TICK_INTERVAL;
    public static final int FIELD_HORIZONTAL_RANGE = InterdictionPulsar.FieldIndices.HORIZONTAL_RANGE;
    public static final int FIELD_VERTICAL_RANGE = InterdictionPulsar.FieldIndices.VERTICAL_RANGE;
    public static final int FIELD_STRENGTH = InterdictionPulsar.FieldIndices.STRENGTH;
    public static final int FIELD_MODE = InterdictionPulsar.FieldIndices.MODE;
    public static final int FIELD_REDSTONE_MODE = InterdictionPulsar.FieldIndices.REDSTONE_MODE;
    public static final int FIELD_AFFECT_HOSTILE = InterdictionPulsar.FieldIndices.AFFECT_HOSTILE;
    public static final int FIELD_AFFECT_PASSIVE = InterdictionPulsar.FieldIndices.AFFECT_PASSIVE;
    public static final int FIELD_AFFECT_PLAYER = InterdictionPulsar.FieldIndices.AFFECT_PLAYER;
    public static final int FIELD_CUSTOM_ORIGIN_X = InterdictionPulsar.FieldIndices.CUSTOM_ORIGIN_X;
    public static final int FIELD_CUSTOM_ORIGIN_Y = InterdictionPulsar.FieldIndices.CUSTOM_ORIGIN_Y;
    public static final int FIELD_CUSTOM_ORIGIN_Z = InterdictionPulsar.FieldIndices.CUSTOM_ORIGIN_Z;
    public static final int FIELD_HORIZONTAL_MODE = InterdictionPulsar.FieldIndices.HORIZONTAL_MODE;
    public static final int FIELD_VERTICAL_MODE = InterdictionPulsar.FieldIndices.VERTICAL_MODE;

    // 按钮ID常量
    public static final int BUTTON_MODE_TOGGLE = 0;
    public static final int BUTTON_REDSTONE_MODE_CYCLE = 1;
    public static final int BUTTON_AFFECT_HOSTILE_TOGGLE = 2;
    public static final int BUTTON_AFFECT_PASSIVE_TOGGLE = 3;
    public static final int BUTTON_AFFECT_PLAYER_TOGGLE = 4;
    public static final int BUTTON_HORIZONTAL_MODE_TOGGLE = 5;
    public static final int BUTTON_VERTICAL_MODE_TOGGLE = 6;

    // 实例字段
    private final ContainerData data;
    private final BlockPos blockPos;

    /**
     * 客户端侧构造函数 - 通过网络数据创建菜单
     */
    public InterdictionPulsarMenu(int id, net.minecraft.world.entity.player.Inventory playerInventory,
                                  net.minecraft.network.FriendlyByteBuf extraData) {
        this(id, extraData.readBlockPos(), new SimpleContainerData(InterdictionPulsar.FieldIndices.FIELD_COUNT));
    }

    /**
     * 主构造函数 - 创建脉冲阻拦器配置菜单
     */
    public InterdictionPulsarMenu(int id, BlockPos blockPos, ContainerData data) {
        super(MenuTypeRegistry.INTERDICTION_PULSAR_MENU.get(), id);
        checkContainerDataCount(data, InterdictionPulsar.FieldIndices.FIELD_COUNT);
        this.data = data;
        this.blockPos = blockPos;
        this.addDataSlots(data);
    }

    /**
     * 处理菜单按钮点击事件 - 修复：确保在服务端执行
     */
    @Override
    public boolean clickMenuButton(Player player, int id) {
        // 这个方法在服务端执行，所以可以直接修改方块实体
        if (player.level().isClientSide) {
            // 客户端只发送网络包
            return handleClientButtonClick(player, id);
        } else {
            // 服务端直接修改方块实体
            return handleServerButtonClick(player, id);
        }
    }

    /**
     * 客户端按钮点击处理 - 发送网络数据包
     */
    private boolean handleClientButtonClick(Player player, int id) {
        int targetField = -1;
        int newValue = 0;

        switch (id) {
            case BUTTON_MODE_TOGGLE:
                targetField = FIELD_MODE;
                newValue = data.get(targetField) == 0 ? 1 : 0;
                break;

            case BUTTON_REDSTONE_MODE_CYCLE:
                targetField = FIELD_REDSTONE_MODE;
                newValue = (data.get(targetField) + 1) % 3;
                break;

            case BUTTON_AFFECT_HOSTILE_TOGGLE:
                targetField = FIELD_AFFECT_HOSTILE;
                newValue = data.get(targetField) == 0 ? 1 : 0;
                break;

            case BUTTON_AFFECT_PASSIVE_TOGGLE:
                targetField = FIELD_AFFECT_PASSIVE;
                newValue = data.get(targetField) == 0 ? 1 : 0;
                break;

            case BUTTON_AFFECT_PLAYER_TOGGLE:
                targetField = FIELD_AFFECT_PLAYER;
                newValue = data.get(targetField) == 0 ? 1 : 0;
                break;

            case BUTTON_HORIZONTAL_MODE_TOGGLE:
                targetField = FIELD_HORIZONTAL_MODE;
                newValue = data.get(targetField) == 0 ? 1 : 0;
                break;

            case BUTTON_VERTICAL_MODE_TOGGLE:
                targetField = FIELD_VERTICAL_MODE;
                newValue = data.get(targetField) == 0 ? 1 : 0;
                break;

            default:
                return false;
        }

        if (targetField != -1) {
            // 发送网络数据包到服务端
            PulsarConfigPacket packet = new PulsarConfigPacket(blockPos, targetField, newValue);
            if (player instanceof net.minecraft.client.player.LocalPlayer localPlayer) {
                localPlayer.connection.send(packet);
            }
            return true;
        }

        return false;
    }

    /**
     * 服务端按钮点击处理 - 直接修改方块实体
     */
    private boolean handleServerButtonClick(Player player, int id) {
        int targetField = -1;
        int newValue = 0;

        switch (id) {
            case BUTTON_MODE_TOGGLE:
                targetField = FIELD_MODE;
                newValue = data.get(targetField) == 0 ? 1 : 0;
                break;

            case BUTTON_REDSTONE_MODE_CYCLE:
                targetField = FIELD_REDSTONE_MODE;
                newValue = (data.get(targetField) + 1) % 3;
                break;

            case BUTTON_AFFECT_HOSTILE_TOGGLE:
                targetField = FIELD_AFFECT_HOSTILE;
                newValue = data.get(targetField) == 0 ? 1 : 0;
                break;

            case BUTTON_AFFECT_PASSIVE_TOGGLE:
                targetField = FIELD_AFFECT_PASSIVE;
                newValue = data.get(targetField) == 0 ? 1 : 0;
                break;

            case BUTTON_AFFECT_PLAYER_TOGGLE:
                targetField = FIELD_AFFECT_PLAYER;
                newValue = data.get(targetField) == 0 ? 1 : 0;
                break;

            case BUTTON_HORIZONTAL_MODE_TOGGLE:
                targetField = FIELD_HORIZONTAL_MODE;
                newValue = data.get(targetField) == 0 ? 1 : 0;
                break;

            case BUTTON_VERTICAL_MODE_TOGGLE:
                targetField = FIELD_VERTICAL_MODE;
                newValue = data.get(targetField) == 0 ? 1 : 0;
                break;

            default:
                return false;
        }

        if (targetField != -1) {
            // 直接修改方块实体
            if (player.level().getBlockEntity(blockPos) instanceof InterdictionPulsar.InterdictionPulsarBlockEntity blockEntity) {
                blockEntity.setField(targetField, newValue);
                blockEntity.setChanged();
            }
            return true;
        }

        return false;
    }

    /**
     * 快速移动物品堆栈 - 这个菜单没有物品栏交互
     */
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    /**
     * 检查玩家是否仍然可以访问这个菜单
     */
    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    /**
     * 获取容器数据
     */
    public ContainerData getData() {
        return data;
    }

    /**
     * 获取方块位置
     */
    public BlockPos getBlockPos() {
        return blockPos;
    }
}