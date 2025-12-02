package com.azurewrath.interdictionpulsar;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

/**
 * 天使方块模块 - 提供空中建筑辅助功能的特殊方块
 */
public class AngelBlock {

    /** 放置距离常量 */
    public static final double PLACEMENT_DISTANCE = 4.5;

    /**
     * 天使方块方块类 - 定义方块在世界中的行为和特性
     */
    public static class AngelBlockBlock extends Block {

        public AngelBlockBlock(Properties properties) {
            super(properties);
        }

        /**
         * 处理玩家破坏方块时的特殊逻辑
         */
        @Override
        public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player,
                                           boolean willHarvest, FluidState fluid) {
            if (!player.isCreative()) {
                player.getInventory().placeItemBackInInventory(
                        BlockandItemRegistry.ANGEL_BLOCK_ITEM.get().getDefaultInstance(),
                        true
                );
            }
            return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
        }
    }

    /**
     * 天使方块物品类 - 定义物品在玩家手中的行为和交互
     */
    public static class AngelBlockItem extends BlockItem {

        public AngelBlockItem(Block block, Properties properties) {
            super(block, properties);
        }

        /**
         * 处理玩家使用物品（右键点击）的核心逻辑
         */
        @Override
        public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
            if (!level.isClientSide()) {
                BlockPos targetPos = calculateTargetPosition(player, PLACEMENT_DISTANCE);

                if (isValidPlacementPosition(level, targetPos)) {
                    level.setBlock(targetPos, BlockandItemRegistry.ANGEL_BLOCK_BLOCK.get().defaultBlockState(), 3);

                    if (!player.isCreative()) {
                        removeItemFromPlayer(player, hand);
                    }
                }
            }

            return super.use(level, player, hand);
        }

        /**
         * 从玩家手中移除一个物品
         */
        private void removeItemFromPlayer(Player player, InteractionHand hand) {
            if (hand == InteractionHand.MAIN_HAND) {
                player.getInventory().removeFromSelected(false);
            } else {
                player.getInventory().removeItem(Inventory.SLOT_OFFHAND, 1);
            }
        }
    }

    /**
     * 检查位置是否有效且可放置
     */
    public static boolean isValidPlacementPosition(Level level, BlockPos pos) {
        return level.isInWorldBounds(pos) && level.getBlockState(pos).canBeReplaced();
    }

    /**
     * 计算目标放置位置
     */
    public static BlockPos calculateTargetPosition(Player player, double distance) {
        double x = player.getX() + player.getLookAngle().x * distance;
        double y = player.getEyeY() + player.getLookAngle().y * distance;
        double z = player.getZ() + player.getLookAngle().z * distance;

        return new BlockPos((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z));
    }

    /**
     * 私有构造函数防止实例化
     */
    private AngelBlock() {
        throw new UnsupportedOperationException("这是一个工具类，不能被实例化");
    }
}