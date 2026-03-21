package io.github.FMG9167.ywna.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ClientEffectHandler {

    public static void onCameraDrift(float yaw, float pitch) {
    }

    public static void onUIFlicker(int durationTicks) {
    }

    public static void onInputHesitation(int delayTicks) {
    }

    public static void onDoppelGlimpse(double x, double y, double z) {
    }
}
