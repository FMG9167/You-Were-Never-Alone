package io.github.FMG9167.ywna.networking;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class YWNAPackets {

    public static final Identifier TEST             = new Identifier("ywna", "test");
    public static final Identifier CAMERA_DRIFT     = new Identifier("ywna", "camera_drift");
    public static final Identifier UI_FLICKER       = new Identifier("ywna", "ui_flicker");
    public static final Identifier INPUT_HESITATION = new Identifier("ywna", "input_hesitation");
    public static final Identifier DOPPEL_GLIMPSE   = new Identifier("ywna", "doppelganger_glimpse");
    public static final Identifier INVENTORY_OPEN   = new Identifier("ywna", "inventory_open");

    public static void sendTest(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, TEST, PacketByteBufs.empty());
    }

    public static void sendCameraDrift(ServerPlayerEntity player, float yaw, float pitch) {
        var buf = PacketByteBufs.create();
        buf.writeFloat(yaw);
        buf.writeFloat(pitch);
        ServerPlayNetworking.send(player, CAMERA_DRIFT, buf);
    }

    public static void sendUIFlicker(ServerPlayerEntity player, int durationTicks) {
        var buf = PacketByteBufs.create();
        buf.writeInt(durationTicks);
        ServerPlayNetworking.send(player, UI_FLICKER, buf);
    }

    public static void sendInputHesitation(ServerPlayerEntity player, int delayTicks) {
        var buf = PacketByteBufs.create();
        buf.writeInt(delayTicks);
        ServerPlayNetworking.send(player, INPUT_HESITATION, buf);
    }

    public static void sendDoppelGlimpse(ServerPlayerEntity player, double x, double y, double z) {
        var buf = PacketByteBufs.create();
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        ServerPlayNetworking.send(player, DOPPEL_GLIMPSE, buf);
    }

    public static void sendInventoryOpen(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, INVENTORY_OPEN, PacketByteBufs.empty());
    }
}
