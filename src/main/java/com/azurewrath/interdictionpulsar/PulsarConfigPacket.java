package com.azurewrath.interdictionpulsar;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * 脉冲阻拦器配置数据包 - 客户端与服务端配置同步
 */
public record PulsarConfigPacket(BlockPos blockPos, int fieldId, int value) implements CustomPacketPayload {

    public static final Type<PulsarConfigPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(InterdictionPulsarMod.MOD_ID, "config"));

    // 修复 StreamCodec 定义
    public static final StreamCodec<FriendlyByteBuf, PulsarConfigPacket> STREAM_CODEC = new StreamCodec<FriendlyByteBuf, PulsarConfigPacket>() {
        @Override
        public PulsarConfigPacket decode(FriendlyByteBuf buf) {
            BlockPos blockPos = buf.readBlockPos();
            int fieldId = buf.readInt();
            int value = buf.readInt();
            return new PulsarConfigPacket(blockPos, fieldId, value);
        }

        @Override
        public void encode(FriendlyByteBuf buf, PulsarConfigPacket packet) {
            buf.writeBlockPos(packet.blockPos);
            buf.writeInt(packet.fieldId);
            buf.writeInt(packet.value);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    /**
     * 在服务端处理接收到的配置数据包
     */
    public static void handle(PulsarConfigPacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Level level = ctx.player().level();
            if (!level.isClientSide && level.isLoaded(msg.blockPos)) {
                if (level.getBlockEntity(msg.blockPos) instanceof InterdictionPulsar.InterdictionPulsarBlockEntity blockEntity) {
                    blockEntity.setField(msg.fieldId, msg.value);
                    blockEntity.setChanged();
                }
            }
        });
    }

    @Override
    public String toString() {
        return String.format("PulsarConfigPacket{pos=%s, field=%d, value=%d}", blockPos, fieldId, value);
    }
}