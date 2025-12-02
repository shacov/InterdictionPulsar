package com.azurewrath.interdictionpulsar;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 脉冲阻拦器主类 - 包含配置字段、方块实体和实体移动逻辑
 */
public class InterdictionPulsar {

    /**
     * 配置字段索引常量
     */
    public static class FieldIndices {
        public static final int TICK_INTERVAL = 0;      // 工作间隔 (1-100 tick)
        public static final int HORIZONTAL_RANGE = 1;   // 水平范围 (1-64 格)
        public static final int VERTICAL_RANGE = 2;     // 垂直范围 (1-16 格)
        public static final int STRENGTH = 3;           // 推力强度 (1-50，对应0.1-5.0)
        public static final int MODE = 4;               // 工作模式 (0=推开, 1=吸引)
        public static final int REDSTONE_MODE = 5;      // 红石模式 (0=忽略, 1=需要, 2=停止)
        public static final int AFFECT_HOSTILE = 6;     // 影响敌对生物 (0=不影响, 1=影响)
        public static final int AFFECT_PASSIVE = 7;     // 影响被动生物 (0=不影响, 1=影响)
        public static final int AFFECT_PLAYER = 8;      // 影响玩家 (0=不影响, 1=影响)
        public static final int CUSTOM_ORIGIN_X = 9;    // 自定义坐标X
        public static final int CUSTOM_ORIGIN_Y = 10;   // 自定义坐标Y
        public static final int CUSTOM_ORIGIN_Z = 11;   // 自定义坐标Z
        public static final int HORIZONTAL_MODE = 12;   // 水平模式 (0=关闭, 1=开启)
        public static final int VERTICAL_MODE = 13;     // 垂直模式 (0=关闭, 1=开启)
        public static final int FIELD_COUNT = 14;       // 配置字段总数
        /**
         * 私有构造函数防止实例化
         */
        private FieldIndices() {
            throw new UnsupportedOperationException("这是一个工具类，不能被实例化");
        }
    }

    // ================================
    // 方块实体类
    // ================================

    /**
     * 脉冲阻拦器方块实体 - 存储配置数据和执行逻辑
     */
    public static class InterdictionPulsarBlockEntity extends BlockEntity implements MenuProvider {

        // NBT标签常量
        private static final String NBT_TIMER = "Timer";
        private static final String NBT_TICK_INTERVAL = "tickInterval";
        private static final String NBT_HORIZONTAL_RANGE = "horizontalRange";
        private static final String NBT_VERTICAL_RANGE = "verticalRange";
        private static final String NBT_STRENGTH = "strength";
        private static final String NBT_MODE = "mode";
        private static final String NBT_REDSTONE_MODE = "redstoneMode";
        private static final String NBT_AFFECT_HOSTILE = "affectHostile";
        private static final String NBT_AFFECT_PASSIVE = "affectPassive";
        private static final String NBT_AFFECT_PLAYER = "affectPlayer";
        private static final String NBT_CUSTOM_ORIGIN_X = "customOriginX";
        private static final String NBT_CUSTOM_ORIGIN_Y = "customOriginY";
        private static final String NBT_CUSTOM_ORIGIN_Z = "customOriginZ";
        private static final String NBT_HORIZONTAL_MODE = "horizontalMode";
        private static final String NBT_VERTICAL_MODE = "verticalMode";

        // 默认配置值
        public static final int DEFAULT_TICK_INTERVAL = 10;
        public static final int DEFAULT_HORIZONTAL_RANGE = 32;
        public static final int DEFAULT_VERTICAL_RANGE = 3;
        public static final float DEFAULT_STRENGTH = 1.4F;

        // 配置范围限制
        private static final int MIN_TICK_INTERVAL = 1;
        private static final int MAX_TICK_INTERVAL = 100;
        private static final int MIN_HORIZONTAL_RANGE = 1;
        private static final int MAX_HORIZONTAL_RANGE = 64;
        private static final int MIN_VERTICAL_RANGE = 1;
        private static final int MAX_VERTICAL_RANGE = 64;
        private static final float MIN_STRENGTH = 0.1f;
        private static final float MAX_STRENGTH = 5.0f;
        private static final int MIN_CUSTOM_ORIGIN = -30000000;
        private static final int MAX_CUSTOM_ORIGIN = 30000000;

        // 实例字段
        private int timer;
        private int tickInterval = DEFAULT_TICK_INTERVAL;
        private int horizontalRange = DEFAULT_HORIZONTAL_RANGE;
        private int verticalRange = DEFAULT_VERTICAL_RANGE;
        private float strength = DEFAULT_STRENGTH;
        private int mode = 0;
        private int redstoneMode = 0;
        private int affectHostile = 1;
        private int affectPassive = 0;
        private int affectPlayer = 0;
        private int customOriginX = 0;
        private int customOriginY = 0;
        private int customOriginZ = 0;
        private int horizontalMode = 1;  // 默认开启水平模式
        private int verticalMode = 0;    // 默认关闭垂直模式

        // 容器数据接口
        private final ContainerData dataAccess = new ContainerData() {
            @Override
            public int get(int index) {
                return getField(index);
            }

            @Override
            public void set(int index, int value) {
                setField(index, value);
            }

            @Override
            public int getCount() {
                return FieldIndices.FIELD_COUNT;
            }
        };

        public InterdictionPulsarBlockEntity(BlockPos pos, BlockState state) {
            super(BlockandItemRegistry.INTERDICTION_PULSAR_BLOCK_ENTITY.get(), pos, state);
            initializeDefaultValues();
        }

        /**
         * 初始化默认配置值
         */
        private void initializeDefaultValues() {
            this.tickInterval = DEFAULT_TICK_INTERVAL;
            this.horizontalRange = DEFAULT_HORIZONTAL_RANGE;
            this.verticalRange = DEFAULT_VERTICAL_RANGE;
            this.strength = DEFAULT_STRENGTH;
            this.mode = 0;
            this.redstoneMode = 0;
            this.affectHostile = 1;
            this.affectPassive = 0;
            this.affectPlayer = 0;

            // 默认自定义坐标为方块自身位置
            BlockPos pos = this.getBlockPos();
            this.customOriginX = pos.getX();
            this.customOriginY = pos.getY();
            this.customOriginZ = pos.getZ();
            this.timer = this.tickInterval;
        }

        /**
         * 方块实体逻辑更新方法（每tick调用）
         */
        public static void tick(Level level, BlockPos pos, BlockState state, InterdictionPulsarBlockEntity blockEntity) {
            if (!blockEntity.checkRedstoneCondition(level, pos)) {
                return;
            }

            blockEntity.timer--;
            if (blockEntity.timer <= 0) {
                blockEntity.timer = blockEntity.tickInterval;

                // 获取作用原点坐标 - 始终使用自定义坐标
                double centerX = blockEntity.customOriginX + 0.5;
                double centerY = blockEntity.customOriginY;
                double centerZ = blockEntity.customOriginZ + 0.5;

                boolean isAttractMode = (blockEntity.mode == 1);

                moveEntityLivingNonplayers(
                        level, centerX, centerY, centerZ,
                        blockEntity.horizontalRange, blockEntity.verticalRange, isAttractMode,
                        blockEntity.strength, blockEntity.affectHostile, blockEntity.affectPassive,
                        blockEntity.affectPlayer,
                        blockEntity.horizontalMode, blockEntity.verticalMode
                );

                blockEntity.timer = blockEntity.tickInterval;
            }
        }

        /**
         * 检查红石条件是否满足
         */
        private boolean checkRedstoneCondition(Level level, BlockPos pos) {
            boolean hasRedstoneSignal = level.hasNeighborSignal(pos);
            switch (redstoneMode) {
                case 0: return true;           // 忽略红石
                case 1: return hasRedstoneSignal; // 需要红石
                case 2: return !hasRedstoneSignal; // 红石停止
                default: return true;
            }
        }

        /**
         * 保存数据到NBT标签 - 修复方法签名
         */
        @Override
        protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
            super.saveAdditional(tag, registries);
            tag.putInt(NBT_TIMER, timer);
            tag.putInt(NBT_TICK_INTERVAL, tickInterval);
            tag.putInt(NBT_HORIZONTAL_RANGE, horizontalRange);
            tag.putInt(NBT_VERTICAL_RANGE, verticalRange);
            tag.putFloat(NBT_STRENGTH, strength);
            tag.putInt(NBT_MODE, mode);
            tag.putInt(NBT_REDSTONE_MODE, redstoneMode);
            tag.putInt(NBT_AFFECT_HOSTILE, affectHostile);
            tag.putInt(NBT_AFFECT_PASSIVE, affectPassive);
            tag.putInt(NBT_AFFECT_PLAYER, affectPlayer);
            tag.putInt(NBT_CUSTOM_ORIGIN_X, customOriginX);
            tag.putInt(NBT_CUSTOM_ORIGIN_Y, customOriginY);
            tag.putInt(NBT_CUSTOM_ORIGIN_Z, customOriginZ);
            tag.putInt(NBT_HORIZONTAL_MODE, horizontalMode);
            tag.putInt(NBT_VERTICAL_MODE, verticalMode);
        }

        /**
         * 从NBT标签加载数据 - 修复方法签名
         */
        @Override
        protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
            super.loadAdditional(tag, registries);
            timer = tag.getInt(NBT_TIMER);
            tickInterval = loadValidatedInt(tag, NBT_TICK_INTERVAL, DEFAULT_TICK_INTERVAL, MIN_TICK_INTERVAL, MAX_TICK_INTERVAL);
            horizontalRange = loadValidatedInt(tag, NBT_HORIZONTAL_RANGE, DEFAULT_HORIZONTAL_RANGE, MIN_HORIZONTAL_RANGE, MAX_HORIZONTAL_RANGE);
            verticalRange = loadValidatedInt(tag, NBT_VERTICAL_RANGE, DEFAULT_VERTICAL_RANGE, MIN_VERTICAL_RANGE, MAX_VERTICAL_RANGE);
            strength = loadValidatedFloat(tag, NBT_STRENGTH, DEFAULT_STRENGTH, MIN_STRENGTH, MAX_STRENGTH);
            mode = loadValidatedInt(tag, NBT_MODE, 0, 0, 1);
            redstoneMode = loadValidatedInt(tag, NBT_REDSTONE_MODE, 0, 0, 2);
            affectHostile = loadValidatedInt(tag, NBT_AFFECT_HOSTILE, 1, 0, 1);
            affectPassive = loadValidatedInt(tag, NBT_AFFECT_PASSIVE, 0, 0, 1);
            affectPlayer = loadValidatedInt(tag, NBT_AFFECT_PLAYER, 0, 0, 1);

            // 加载自定义坐标，如果没有则使用方块自身位置
            BlockPos pos = this.getBlockPos();
            customOriginX = loadValidatedInt(tag, NBT_CUSTOM_ORIGIN_X, pos.getX(), MIN_CUSTOM_ORIGIN, MAX_CUSTOM_ORIGIN);
            customOriginY = loadValidatedInt(tag, NBT_CUSTOM_ORIGIN_Y, pos.getY(), MIN_CUSTOM_ORIGIN, MAX_CUSTOM_ORIGIN);
            customOriginZ = loadValidatedInt(tag, NBT_CUSTOM_ORIGIN_Z, pos.getZ(), MIN_CUSTOM_ORIGIN, MAX_CUSTOM_ORIGIN);

            horizontalMode = loadValidatedInt(tag, NBT_HORIZONTAL_MODE, 1, 0, 1);
            verticalMode = loadValidatedInt(tag, NBT_VERTICAL_MODE, 0, 0, 1);
        }

        /**
         * 加载并验证整数NBT值
         */
        private int loadValidatedInt(CompoundTag tag, String key, int defaultValue, int minValue, int maxValue) {
            if (tag.contains(key)) {
                int value = tag.getInt(key);
                return Math.max(minValue, Math.min(maxValue, value));
            }
            return defaultValue;
        }

        /**
         * 加载并验证浮点数NBT值
         */
        private float loadValidatedFloat(CompoundTag tag, String key, float defaultValue, float minValue, float maxValue) {
            if (tag.contains(key)) {
                float value = tag.getFloat(key);
                return Math.max(minValue, Math.min(maxValue, value));
            }
            return defaultValue;
        }

        /**
         * 获取指定字段的当前值
         */
        public int getField(int id) {
            return switch (id) {
                case FieldIndices.TICK_INTERVAL -> tickInterval;
                case FieldIndices.HORIZONTAL_RANGE -> horizontalRange;
                case FieldIndices.VERTICAL_RANGE -> verticalRange;
                case FieldIndices.STRENGTH -> (int) (strength * 10);
                case FieldIndices.MODE -> mode;
                case FieldIndices.REDSTONE_MODE -> redstoneMode;
                case FieldIndices.AFFECT_HOSTILE -> affectHostile;
                case FieldIndices.AFFECT_PASSIVE -> affectPassive;
                case FieldIndices.AFFECT_PLAYER -> affectPlayer;
                case FieldIndices.CUSTOM_ORIGIN_X -> customOriginX;
                case FieldIndices.CUSTOM_ORIGIN_Y -> customOriginY;
                case FieldIndices.CUSTOM_ORIGIN_Z -> customOriginZ;
                case FieldIndices.HORIZONTAL_MODE -> horizontalMode;
                case FieldIndices.VERTICAL_MODE -> verticalMode;
                default -> 0;
            };
        }

        /**
         * 设置指定字段的值
         */
        public void setField(int id, int value) {
            switch (id) {
                case FieldIndices.TICK_INTERVAL:
                    this.tickInterval = Math.max(MIN_TICK_INTERVAL, Math.min(MAX_TICK_INTERVAL, value));
                    break;
                case FieldIndices.HORIZONTAL_RANGE:
                    this.horizontalRange = Math.max(MIN_HORIZONTAL_RANGE, Math.min(MAX_HORIZONTAL_RANGE, value));
                    break;
                case FieldIndices.VERTICAL_RANGE:
                    this.verticalRange = Math.max(MIN_VERTICAL_RANGE, Math.min(MAX_VERTICAL_RANGE, value));
                    break;
                case FieldIndices.STRENGTH:
                    this.strength = Math.max(MIN_STRENGTH, Math.min(MAX_STRENGTH, value / 10.0f));
                    break;
                case FieldIndices.MODE:
                    this.mode = Math.max(0, Math.min(1, value));
                    break;
                case FieldIndices.REDSTONE_MODE:
                    this.redstoneMode = Math.max(0, Math.min(2, value));
                    break;
                case FieldIndices.AFFECT_HOSTILE:
                    this.affectHostile = Math.max(0, Math.min(1, value));
                    break;
                case FieldIndices.AFFECT_PASSIVE:
                    this.affectPassive = Math.max(0, Math.min(1, value));
                    break;
                case FieldIndices.AFFECT_PLAYER:
                    this.affectPlayer = Math.max(0, Math.min(1, value));
                    break;
                case FieldIndices.CUSTOM_ORIGIN_X:
                    this.customOriginX = Math.max(MIN_CUSTOM_ORIGIN, Math.min(MAX_CUSTOM_ORIGIN, value));
                    break;
                case FieldIndices.CUSTOM_ORIGIN_Y:
                    this.customOriginY = Math.max(MIN_CUSTOM_ORIGIN, Math.min(MAX_CUSTOM_ORIGIN, value));
                    break;
                case FieldIndices.CUSTOM_ORIGIN_Z:
                    this.customOriginZ = Math.max(MIN_CUSTOM_ORIGIN, Math.min(MAX_CUSTOM_ORIGIN, value));
                    break;
                case FieldIndices.HORIZONTAL_MODE:
                    this.horizontalMode = Math.max(0, Math.min(1, value));
                    break;
                case FieldIndices.VERTICAL_MODE:
                    this.verticalMode = Math.max(0, Math.min(1, value));
                    break;
            }
            this.setChanged();
        }

        // GUI菜单提供者实现
        @Override
        public Component getDisplayName() {
            return Component.translatable("block.interdictionpulsar.interdiction_pulsar");
        }

        @Nullable
        @Override
        public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
            updateContainerData();
            return new InterdictionPulsarMenu(id, this.worldPosition, this.dataAccess);
        }

        /**
         * 更新容器数据以同步到GUI
         */
        private void updateContainerData() {
            this.dataAccess.set(InterdictionPulsarMenu.FIELD_TICK_INTERVAL, this.tickInterval);
            this.dataAccess.set(InterdictionPulsarMenu.FIELD_HORIZONTAL_RANGE, this.horizontalRange);
            this.dataAccess.set(InterdictionPulsarMenu.FIELD_VERTICAL_RANGE, this.verticalRange);
            this.dataAccess.set(InterdictionPulsarMenu.FIELD_STRENGTH, (int)(this.strength * 10));
            this.dataAccess.set(InterdictionPulsarMenu.FIELD_MODE, this.mode);
            this.dataAccess.set(InterdictionPulsarMenu.FIELD_REDSTONE_MODE, this.redstoneMode);
            this.dataAccess.set(InterdictionPulsarMenu.FIELD_AFFECT_HOSTILE, this.affectHostile);
            this.dataAccess.set(InterdictionPulsarMenu.FIELD_AFFECT_PASSIVE, this.affectPassive);
            this.dataAccess.set(InterdictionPulsarMenu.FIELD_AFFECT_PLAYER, this.affectPlayer);
            this.dataAccess.set(InterdictionPulsarMenu.FIELD_CUSTOM_ORIGIN_X, this.customOriginX);
            this.dataAccess.set(InterdictionPulsarMenu.FIELD_CUSTOM_ORIGIN_Y, this.customOriginY);
            this.dataAccess.set(InterdictionPulsarMenu.FIELD_CUSTOM_ORIGIN_Z, this.customOriginZ);
        }
    }

    // ================================
    // 方块类
    // ================================

    /**
     * 脉冲阻拦器方块 - 定义方块行为和用户交互
     */
    public static class InterdictionPulsarBlock extends Block implements EntityBlock {

        public InterdictionPulsarBlock(Properties properties) {
            super(properties);
        }

        /**
         * 创建新的方块实体实例
         */
        @Nullable
        @Override
        public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
            return new InterdictionPulsarBlockEntity(pos, state);
        }

        /**
         * 获取方块实体更新器
         */
        @Nullable
        @Override
        public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
            if (level.isClientSide) {
                return null;
            }
            if (type == BlockandItemRegistry.INTERDICTION_PULSAR_BLOCK_ENTITY.get()) {
                @SuppressWarnings("unchecked")
                BlockEntityTicker<T> castedTicker = (BlockEntityTicker<T>) (BlockEntityTicker<InterdictionPulsarBlockEntity>) InterdictionPulsarBlockEntity::tick;
                return castedTicker;
            }
            return null;
        }

        /**
         * 玩家破坏方块时的处理
         */
        @Override
        public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @javax.annotation.Nullable BlockEntity blockEntity, ItemStack tool) {
            super.playerDestroy(level, player, pos, state, blockEntity, tool);
        }

        /**
         * 处理玩家与方块的交互（右键点击） - 修复方法签名
         */
        @Override
        protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
            if (level.isClientSide) {
                return InteractionResult.SUCCESS;
            } else {
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof InterdictionPulsarBlockEntity) {
                    // 使用新的打开屏幕方式
                    if (player instanceof ServerPlayer serverPlayer) {
                        serverPlayer.openMenu((MenuProvider) blockEntity, pos);
                    }
                }
                // 聊天框输出方块参数信息，可供调试时使用
                // displayConfigurationInfo(level, pos, player);
                return InteractionResult.CONSUME;
            }
        }

        /**
         * 处理带物品的交互 - 使用正确的返回值类型
         */
        @Override
        protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
            // 如果希望在有物品时也有特殊行为，可以在这里实现
            // 否则，调用父类实现
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }

        /**
         * 在聊天栏显示脉冲阻拦器的当前配置信息
         */
        private void displayConfigurationInfo(Level level, BlockPos pos, Player player) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (!(blockEntity instanceof InterdictionPulsarBlockEntity pulsar)) {
                return;
            }

            String modeText = (pulsar.getField(FieldIndices.MODE) == 0) ? "推开" : "吸引";
            String redstoneModeText = getRedstoneModeText(pulsar.getField(FieldIndices.REDSTONE_MODE));
            String hostileStatus = (pulsar.getField(FieldIndices.AFFECT_HOSTILE) == 1) ? "§a是" : "§c否";
            String passiveStatus = (pulsar.getField(FieldIndices.AFFECT_PASSIVE) == 1) ? "§a是" : "§c否";
            String playerStatus = (pulsar.getField(FieldIndices.AFFECT_PLAYER) == 1) ? "§a是" : "§c否";

            // 直接显示自定义坐标
            String originText = String.format("(%d, %d, %d)",
                    pulsar.getField(FieldIndices.CUSTOM_ORIGIN_X),
                    pulsar.getField(FieldIndices.CUSTOM_ORIGIN_Y),
                    pulsar.getField(FieldIndices.CUSTOM_ORIGIN_Z));

            Component message = Component.literal(
                    "§6脉冲阻拦器配置§r\n" +
                            "§7工作间隔: §f" + pulsar.getField(FieldIndices.TICK_INTERVAL) + " tick\n" +
                            "§7水平范围: §f" + pulsar.getField(FieldIndices.HORIZONTAL_RANGE) + " 格\n" +
                            "§7垂直范围: §f" + pulsar.getField(FieldIndices.VERTICAL_RANGE) + " 格\n" +
                            "§7推力强度: §f" + (pulsar.getField(FieldIndices.STRENGTH) / 10.0f) + "\n" +
                            "§7工作模式: §f" + modeText + "\n" +
                            "§7红石模式: §f" + redstoneModeText + "\n" +
                            "§7作用原点: §f" + originText + "\n" +
                            "§7影响生物类型:\n" +
                            "  §7敌对生物: " + hostileStatus + "§r\n" +
                            "  §7被动生物: " + passiveStatus + "§r\n" +
                            "  §7玩家: " + playerStatus
            );

            player.displayClientMessage(message, false);
        }

        /**
         * 获取红石模式的文本描述
         */
        private String getRedstoneModeText(int redstoneMode) {
            return switch (redstoneMode) {
                case 0 -> "忽略红石";
                case 1 -> "需要红石";
                case 2 -> "红石停止";
                default -> "未知";
            };
        }
    }

    // ================================
    // 实体移动逻辑
    // ================================

    /** 最小移动距离常量 */
    private static final double MIN_MOVE_DISTANCE = 0.4;

    /** 速度切换距离常量 */
    private static final double SPEED_CUTOFF_DISTANCE = 3.0;

    /** 玩家速度乘数 */
    private static final float PLAYER_SPEED_MULTIPLIER = 2.0f;

    /**
     * 移动范围内的生物实体
     */
    public static int moveEntityLivingNonplayers(Level world, double x, double y, double z,
                                                 int hRadius, int vRadius, boolean towardsPos, float speed,
                                                 int affectHostile, int affectPassive, int affectPlayer,
                                                 int horizontalMode, int verticalMode) {
        AABB range = makeBoundingBox(x, y, z, hRadius, vRadius);
        List<LivingEntity> affectedEntities = getAffectedEntities(world, range, affectHostile, affectPassive, affectPlayer);
        return applyMovementToEntities(x, y, z, towardsPos, affectedEntities, speed, speed, horizontalMode, verticalMode);
    }

    /**
     * 创建作用范围的边界框
     */
    public static AABB makeBoundingBox(double x, double y, double z, int hRadius, int vRadius) {
        return new AABB(
                x - hRadius, y - vRadius, z - hRadius,
                x + hRadius, y + vRadius, z + hRadius
        );
    }

    /**
     * 获取范围内受影响的生物实体列表
     */
    public static List<LivingEntity> getAffectedEntities(Level world, AABB range,
                                                         int affectHostile, int affectPassive, int affectPlayer) {
        List<LivingEntity> allEntities = world.getEntitiesOfClass(LivingEntity.class, range);
        allEntities.removeIf(entity -> {
            if (entity instanceof Player) {
                return affectPlayer == 0;
            }
            MobCategory category = entity.getType().getCategory();
            if (category == MobCategory.MONSTER) {
                return affectHostile == 0;
            } else {
                return affectPassive == 0;
            }
        });
        return allEntities;
    }

    /**
     * 应用移动力到实体列表
     */
    public static int applyMovementToEntities(double x, double y, double z, boolean towardsPos,
                                              List<? extends Entity> entities, float speedClose, float speedFar,
                                              int horizontalMode, int verticalMode) {
        int movedCount = 0;

        // 如果没有开启任何模式，直接返回
        if (horizontalMode == 0 && verticalMode == 0) {
            return 0;
        }

        for (Entity entity : entities) {
            if (entity == null) continue;

            // 检查玩家潜行状态
            if (entity instanceof Player player) {
                if (player.isCrouching()) {
                    continue;
                }
            }

            // 计算三维距离向量
            double deltaX = x - entity.getX();
            double deltaY = y - entity.getY();
            double deltaZ = z - entity.getZ();

            double horizontalDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
            double verticalDistance = Math.abs(deltaY);

            // 计算总距离（用于速度选择）
            double totalDistance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

            if (totalDistance > MIN_MOVE_DISTANCE) {
                // 根据总距离选择速度系数
                float currentSpeed = (totalDistance > SPEED_CUTOFF_DISTANCE) ? speedFar : speedClose;

                // 玩家使用更高的速度系数
                if (entity instanceof Player) {
                    currentSpeed *= PLAYER_SPEED_MULTIPLIER;
                }

                // 计算运动向量
                double motionX = 0;
                double motionY = 0;
                double motionZ = 0;

                if (horizontalMode == 1 && horizontalDistance > MIN_MOVE_DISTANCE) {
                    // 计算水平方向
                    double horizontalDirection = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
                    if (horizontalDirection > 0) {
                        double normalizedX = deltaX / horizontalDirection;
                        double normalizedZ = deltaZ / horizontalDirection;
                        double horizontalMultiplier = towardsPos ? currentSpeed : -currentSpeed;

                        motionX = normalizedX * horizontalMultiplier;
                        motionZ = normalizedZ * horizontalMultiplier;
                    }
                }

                if (verticalMode == 1 && verticalDistance > MIN_MOVE_DISTANCE) {
                    // 计算垂直方向
                    double verticalMultiplier = towardsPos ? currentSpeed : -currentSpeed;
                    motionY = (deltaY > 0 ? 1 : -1) * verticalMultiplier;
                }

                // 应用移动力到实体
                if (motionX != 0 || motionY != 0 || motionZ != 0) {
                    applyEntityMovement(entity, motionX, motionY, motionZ);
                    movedCount++;
                }
            }
        }

        return movedCount;
    }

    /**
     * 应用移动力到单个实体
     */
    private static void applyEntityMovement(Entity entity, double motionX, double motionY, double motionZ) {
        if (entity instanceof Player player) {
            player.setDeltaMovement(motionX, player.getDeltaMovement().y + motionY, motionZ);
            player.hurtMarked = true;
        } else {
            entity.setDeltaMovement(motionX, entity.getDeltaMovement().y + motionY, motionZ);
        }
    }

    /**
     * 私有构造函数防止实例化
     */
    private InterdictionPulsar() {
        throw new UnsupportedOperationException("这是一个工具类，不能被实例化");
    }
}