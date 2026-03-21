package io.github.FMG9167.ywna.client;

import io.github.FMG9167.ywna.YWNAMod;
import io.github.FMG9167.ywna.networking.YWNAPackets;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class YWNAModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        YWNAMod.LOGGER.info("[YWNA] Initializing Client...]");

        ClientPlayNetworking.registerGlobalReceiver(YWNAPackets.TEST,
                (client, handler, buf, responseSender) -> {
            client.execute(() -> {
                YWNAMod.LOGGER.info("[YWNA] Test packet received on client");
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(YWNAPackets.CAMERA_DRIFT,
                (client, handler, buf, responseSender) -> {
            float yaw = buf.readFloat();
            float pitch = buf.readFloat();
            client.execute(() -> {
                YWNAMod.LOGGER.info("[YWNA] Camera drift packet received: yaw = {} , pitch = {}", yaw, pitch);
                ClientEffectHandler.onCameraDrift(yaw, pitch);
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(YWNAPackets.UI_FLICKER,
                (client, handler, buf, responseSender) -> {
            int durationTicks =  buf.readInt();
            client.execute(() -> {
                YWNAMod.LOGGER.info("[YWNA] UI flicker packet received: durationTicks = {}", durationTicks);
                ClientEffectHandler.onUIFlicker(durationTicks);
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(YWNAPackets.INPUT_HESITATION,
                (client, handler, buf, responseSender) -> {
            int delayTicks = buf.readInt();
            client.execute(() -> {
                YWNAMod.LOGGER.info("[YWNA] Input Hesitation packet received: delayTicks = {}", delayTicks);
                ClientEffectHandler.onInputHesitation(delayTicks);
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(YWNAPackets.DOPPEL_GLIMPSE,
                (client, handler, buf, responseSender) -> {
            double x = buf.readDouble();
            double y = buf.readDouble();
            double z = buf.readDouble();
            client.execute(() -> {
                YWNAMod.LOGGER.info("[YWNA] Doppelganger Glimpse  packet received: x = {} y = {} z = {}", x, y, z);
                ClientEffectHandler.onDoppelGlimpse(x, y, z);
            });
        });

        YWNAMod.LOGGER.info("[YWNA] Client Initialized.");
    }
}
