package io.github.FMG9167.ywna.mixin;

import io.github.FMG9167.ywna.profile.PlayerProfile;
import io.github.FMG9167.ywna.profile.ProfileManager;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.OptionalInt;

@Mixin(ServerPlayerEntity.class)
public class ContainerOpenMixin {

    @Inject(method = "openHandledScreen", at = @At("HEAD"))
    private void openHandledScreen(NamedScreenHandlerFactory factory, CallbackInfoReturnable<OptionalInt> cir){
        ServerPlayerEntity self = (ServerPlayerEntity)(Object)this;
        PlayerProfile profile = ProfileManager.get(self);
    }
}