package com.azurewrath.interdictionpulsar;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/**
 * 脉冲阻拦器命令处理器 - 通过命令配置脉冲阻拦器
 */
@EventBusSubscriber(modid = InterdictionPulsarMod.MOD_ID)
public class PulsarCommand {

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(buildCommandTree());
    }

    /**
     * 构建命令结构树
     */
    private static LiteralArgumentBuilder<CommandSourceStack> buildCommandTree() {
        return Commands.literal("pulsar")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("set")
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .then(buildSetCommands())
                        )
                )
                .then(Commands.literal("get")
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .executes(context -> getConfig(
                                        context.getSource(),
                                        BlockPosArgument.getLoadedBlockPos(context, "pos")
                                ))
                        )
                );
    }

    /**
     * 构建设置命令的子命令
     */
    private static LiteralArgumentBuilder<CommandSourceStack> buildSetCommands() {
        return Commands.literal("")
                .then(Commands.literal("mode")
                        .then(Commands.argument("mode", IntegerArgumentType.integer(0, 1))
                                .executes(context -> setMode(
                                        context.getSource(),
                                        BlockPosArgument.getLoadedBlockPos(context, "pos"),
                                        IntegerArgumentType.getInteger(context, "mode")
                                ))
                        )
                )
                .then(Commands.literal("redstone")
                        .then(Commands.argument("mode", IntegerArgumentType.integer(0, 2))
                                .executes(context -> setRedstoneMode(
                                        context.getSource(),
                                        BlockPosArgument.getLoadedBlockPos(context, "pos"),
                                        IntegerArgumentType.getInteger(context, "mode")
                                ))
                        )
                )
                .then(Commands.literal("tickInterval")
                        .then(Commands.argument("value", IntegerArgumentType.integer(1, 100))
                                .executes(context -> setTickInterval(
                                        context.getSource(),
                                        BlockPosArgument.getLoadedBlockPos(context, "pos"),
                                        IntegerArgumentType.getInteger(context, "value")
                                ))
                        )
                )
                .then(Commands.literal("horizontalRange")
                        .then(Commands.argument("value", IntegerArgumentType.integer(1, 64))
                                .executes(context -> setHorizontalRange(
                                        context.getSource(),
                                        BlockPosArgument.getLoadedBlockPos(context, "pos"),
                                        IntegerArgumentType.getInteger(context, "value")
                                ))
                        )
                )
                .then(Commands.literal("verticalRange")
                        .then(Commands.argument("value", IntegerArgumentType.integer(1, 64))
                                .executes(context -> setVerticalRange(
                                        context.getSource(),
                                        BlockPosArgument.getLoadedBlockPos(context, "pos"),
                                        IntegerArgumentType.getInteger(context, "value")
                                ))
                        )
                )
                .then(Commands.literal("strength")
                        .then(Commands.argument("value", IntegerArgumentType.integer(1, 50))
                                .executes(context -> setStrength(
                                        context.getSource(),
                                        BlockPosArgument.getLoadedBlockPos(context, "pos"),
                                        IntegerArgumentType.getInteger(context, "value")
                                ))
                        )
                )
                .then(Commands.literal("hostile")
                        .then(Commands.argument("enabled", IntegerArgumentType.integer(0, 1))
                                .executes(context -> setAffectHostile(
                                        context.getSource(),
                                        BlockPosArgument.getLoadedBlockPos(context, "pos"),
                                        IntegerArgumentType.getInteger(context, "enabled")
                                ))
                        )
                )
                .then(Commands.literal("passive")
                        .then(Commands.argument("enabled", IntegerArgumentType.integer(0, 1))
                                .executes(context -> setAffectPassive(
                                        context.getSource(),
                                        BlockPosArgument.getLoadedBlockPos(context, "pos"),
                                        IntegerArgumentType.getInteger(context, "enabled")
                                ))
                        )
                )
                .then(Commands.literal("player")
                        .then(Commands.argument("enabled", IntegerArgumentType.integer(0, 1))
                                .executes(context -> setAffectPlayer(
                                        context.getSource(),
                                        BlockPosArgument.getLoadedBlockPos(context, "pos"),
                                        IntegerArgumentType.getInteger(context, "enabled")
                                ))
                        )
                )
                .then(Commands.literal("originX")
                        .then(Commands.argument("value", IntegerArgumentType.integer(-30000000, 30000000))
                                .executes(context -> setCustomOriginX(
                                        context.getSource(),
                                        BlockPosArgument.getLoadedBlockPos(context, "pos"),
                                        IntegerArgumentType.getInteger(context, "value")
                                ))
                        )
                )
                .then(Commands.literal("originY")
                        .then(Commands.argument("value", IntegerArgumentType.integer(-30000000, 30000000))
                                .executes(context -> setCustomOriginY(
                                        context.getSource(),
                                        BlockPosArgument.getLoadedBlockPos(context, "pos"),
                                        IntegerArgumentType.getInteger(context, "value")
                                ))
                        )
                )
                .then(Commands.literal("originZ")
                        .then(Commands.argument("value", IntegerArgumentType.integer(-30000000, 30000000))
                                .executes(context -> setCustomOriginZ(
                                        context.getSource(),
                                        BlockPosArgument.getLoadedBlockPos(context, "pos"),
                                        IntegerArgumentType.getInteger(context, "value")
                                ))
                        )
                )
                .then(Commands.literal("horizontalMode")
                        .then(Commands.argument("enabled", IntegerArgumentType.integer(0, 1))
                                .executes(context -> setHorizontalMode(
                                        context.getSource(),
                                        BlockPosArgument.getLoadedBlockPos(context, "pos"),
                                        IntegerArgumentType.getInteger(context, "enabled")
                                ))
                        )
                )
                .then(Commands.literal("verticalMode")
                        .then(Commands.argument("enabled", IntegerArgumentType.integer(0, 1))
                                .executes(context -> setVerticalMode(
                                        context.getSource(),
                                        BlockPosArgument.getLoadedBlockPos(context, "pos"),
                                        IntegerArgumentType.getInteger(context, "enabled")
                                ))
                        )
                )
                ;

    }

    // ================================
    // 配置设置方法
    // ================================

    /**
     * 设置工作模式
     */
    private static int setMode(CommandSourceStack source, BlockPos pos, int mode) {
        return setFieldWithMessage(source, pos, InterdictionPulsar.FieldIndices.MODE, mode,
                "已将脉冲阻拦器模式设置为: " + (mode == 0 ? "推开" : "吸引"));
    }

    /**
     * 设置红石模式
     */
    private static int setRedstoneMode(CommandSourceStack source, BlockPos pos, int redstoneMode) {
        String modeDescription = getRedstoneModeDescription(redstoneMode);
        return setFieldWithMessage(source, pos, InterdictionPulsar.FieldIndices.REDSTONE_MODE, redstoneMode,
                "已将红石模式设置为: " + modeDescription);
    }

    /**
     * 设置工作间隔
     */
    private static int setTickInterval(CommandSourceStack source, BlockPos pos, int value) {
        return setFieldWithMessage(source, pos, InterdictionPulsar.FieldIndices.TICK_INTERVAL, value,
                "已将工作间隔设置为: " + value + " tick");
    }

    /**
     * 设置水平范围
     */
    private static int setHorizontalRange(CommandSourceStack source, BlockPos pos, int value) {
        return setFieldWithMessage(source, pos, InterdictionPulsar.FieldIndices.HORIZONTAL_RANGE, value,
                "已将水平范围设置为: " + value + " 格");
    }

    /**
     * 设置垂直范围
     */
    private static int setVerticalRange(CommandSourceStack source, BlockPos pos, int value) {
        return setFieldWithMessage(source, pos, InterdictionPulsar.FieldIndices.VERTICAL_RANGE, value,
                "已将垂直范围设置为: " + value + " 格");
    }

    /**
     * 设置推力强度
     */
    private static int setStrength(CommandSourceStack source, BlockPos pos, int value) {
        float actualStrength = value / 10.0f;
        return setFieldWithMessage(source, pos, InterdictionPulsar.FieldIndices.STRENGTH, value,
                "已将推力强度设置为: " + actualStrength);
    }

    // ================================
    // 生物类型筛选设置方法
    // ================================

    /**
     * 设置影响敌对生物
     */
    private static int setAffectHostile(CommandSourceStack source, BlockPos pos, int enabled) {
        String status = (enabled == 1) ? "开启" : "关闭";
        return setFieldWithMessage(source, pos, InterdictionPulsar.FieldIndices.AFFECT_HOSTILE, enabled,
                "已" + status + "影响敌对生物");
    }

    /**
     * 设置影响被动生物
     */
    private static int setAffectPassive(CommandSourceStack source, BlockPos pos, int enabled) {
        String status = (enabled == 1) ? "开启" : "关闭";
        return setFieldWithMessage(source, pos, InterdictionPulsar.FieldIndices.AFFECT_PASSIVE, enabled,
                "已" + status + "影响被动生物");
    }

    /**
     * 设置影响玩家
     */
    private static int setAffectPlayer(CommandSourceStack source, BlockPos pos, int enabled) {
        String status = (enabled == 1) ? "开启" : "关闭";
        return setFieldWithMessage(source, pos, InterdictionPulsar.FieldIndices.AFFECT_PLAYER, enabled,
                "已" + status + "影响玩家");
    }

    // ================================
    // 自定义坐标设置方法
    // ================================

    /**
     * 设置自定义坐标X
     */
    private static int setCustomOriginX(CommandSourceStack source, BlockPos pos, int value) {
        return setFieldWithMessage(source, pos, InterdictionPulsar.FieldIndices.CUSTOM_ORIGIN_X, value,
                "已将自定义坐标X设置为: " + value);
    }

    /**
     * 设置自定义坐标Y
     */
    private static int setCustomOriginY(CommandSourceStack source, BlockPos pos, int value) {
        return setFieldWithMessage(source, pos, InterdictionPulsar.FieldIndices.CUSTOM_ORIGIN_Y, value,
                "已将自定义坐标Y设置为: " + value);
    }

    /**
     * 设置自定义坐标Z
     */
    private static int setCustomOriginZ(CommandSourceStack source, BlockPos pos, int value) {
        return setFieldWithMessage(source, pos, InterdictionPulsar.FieldIndices.CUSTOM_ORIGIN_Z, value,
                "已将自定义坐标Z设置为: " + value);
    }

    // ================================
    // 作用力方向设置方法
    // ================================

    /**
     * 设置水平模式
     */
    private static int setHorizontalMode(CommandSourceStack source, BlockPos pos, int enabled) {
        String status = (enabled == 1) ? "开启" : "关闭";
        return setFieldWithMessage(source, pos, InterdictionPulsar.FieldIndices.HORIZONTAL_MODE, enabled,
                "已" + status + "水平模式");
    }

    /**
     * 设置垂直模式
     */
    private static int setVerticalMode(CommandSourceStack source, BlockPos pos, int enabled) {
        String status = (enabled == 1) ? "开启" : "关闭";
        return setFieldWithMessage(source, pos, InterdictionPulsar.FieldIndices.VERTICAL_MODE, enabled,
                "已" + status + "垂直模式");
    }

    // ================================
    // 配置查看方法
    // ================================

    /**
     * 查看配置信息
     */
    private static int getConfig(CommandSourceStack source, BlockPos pos) {
        if (source.getLevel().getBlockEntity(pos) instanceof InterdictionPulsar.InterdictionPulsarBlockEntity blockEntity) {
            Component configMessage = buildConfigMessage(blockEntity);
            source.sendSuccess(() -> configMessage, false);
            return Command.SINGLE_SUCCESS;
        }
        source.sendFailure(Component.literal("§c指定位置没有脉冲阻拦器"));
        return 0;
    }

    // ================================
    // 工具方法
    // ================================

    /**
     * 通用字段设置方法
     */
    private static int setFieldWithMessage(CommandSourceStack source, BlockPos pos, int fieldId, int value, String message) {
        if (source.getLevel().getBlockEntity(pos) instanceof InterdictionPulsar.InterdictionPulsarBlockEntity blockEntity) {
            blockEntity.setField(fieldId, value);
            source.sendSuccess(() -> Component.literal("§a" + message), true);
            return Command.SINGLE_SUCCESS;
        }
        source.sendFailure(Component.literal("§c指定位置没有脉冲阻拦器"));
        return 0;
    }

    /**
     * 获取红石模式的文本描述
     */
    private static String getRedstoneModeDescription(int redstoneMode) {
        return switch (redstoneMode) {
            case 0 -> "忽略红石";
            case 1 -> "需要红石";
            case 2 -> "红石停止";
            default -> "未知";
        };
    }

    /**
     * 构建配置信息消息
     */
    private static Component buildConfigMessage(InterdictionPulsar.InterdictionPulsarBlockEntity blockEntity) {
        String modeDescription = (blockEntity.getField(InterdictionPulsar.FieldIndices.MODE) == 0) ? "推开" : "吸引";
        String redstoneModeDescription = getRedstoneModeDescription(
                blockEntity.getField(InterdictionPulsar.FieldIndices.REDSTONE_MODE)
        );
        String hostileStatus = (blockEntity.getField(InterdictionPulsar.FieldIndices.AFFECT_HOSTILE) == 1) ? "§a是" : "§c否";
        String passiveStatus = (blockEntity.getField(InterdictionPulsar.FieldIndices.AFFECT_PASSIVE) == 1) ? "§a是" : "§c否";
        String playerStatus = (blockEntity.getField(InterdictionPulsar.FieldIndices.AFFECT_PLAYER) == 1) ? "§a是" : "§c否";

        // 直接显示自定义坐标
        String originText = String.format("(%d, %d, %d)",
                blockEntity.getField(InterdictionPulsar.FieldIndices.CUSTOM_ORIGIN_X),
                blockEntity.getField(InterdictionPulsar.FieldIndices.CUSTOM_ORIGIN_Y),
                blockEntity.getField(InterdictionPulsar.FieldIndices.CUSTOM_ORIGIN_Z));

        String horizontalModeStatus = (blockEntity.getField(InterdictionPulsar.FieldIndices.HORIZONTAL_MODE) == 1) ? "§a开启" : "§c关闭";
        String verticalModeStatus = (blockEntity.getField(InterdictionPulsar.FieldIndices.VERTICAL_MODE) == 1) ? "§a开启" : "§c关闭";

        String configText =
                "§6脉冲阻拦器配置§r\n" +
                        "§7工作间隔: §f" + blockEntity.getField(InterdictionPulsar.FieldIndices.TICK_INTERVAL) + " tick\n" +
                        "§7水平范围: §f" + blockEntity.getField(InterdictionPulsar.FieldIndices.HORIZONTAL_RANGE) + " 格\n" +
                        "§7垂直范围: §f" + blockEntity.getField(InterdictionPulsar.FieldIndices.VERTICAL_RANGE) + " 格\n" +
                        "§7推力强度: §f" + (blockEntity.getField(InterdictionPulsar.FieldIndices.STRENGTH) / 10.0f) + "\n" +
                        "§7工作模式: §f" + modeDescription + "\n" +
                        "§7红石模式: §f" + redstoneModeDescription + "\n" +
                        "§7作用原点: §f" + originText + "\n" +
                        "§7影响生物类型:\n" +
                        "  §7敌对生物: " + hostileStatus + "§r\n" +
                        "  §7被动生物: " + passiveStatus + "§r\n" +
                        "  §7玩家: " + playerStatus + "§r\n" +
                        "§7移动模式:\n" +
                        "  §7水平模式: " + horizontalModeStatus + "§r\n" +
                        "  §7垂直模式: " + verticalModeStatus + "§r\n"
                ;

        return Component.literal(configText);
    }

    /**
     * 私有构造函数防止实例化
     */
    private PulsarCommand() {
        throw new UnsupportedOperationException("这是一个工具类，不能被实例化");
    }
}