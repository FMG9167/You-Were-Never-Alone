package io.github.FMG9167.ywna.mixin;

import io.github.FMG9167.ywna.YWNAMod;
import io.github.FMG9167.ywna.profile.ProfileManager;
import io.github.FMG9167.ywna.tracking.BehaviorTracker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ProfileNbtMixin {

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        YWNAMod.LOGGER.info("[YWNA] Reading NBT data with key {}", nbt.contains(ProfileManager.NBT_KEY));
        ServerPlayerEntity self = (ServerPlayerEntity) (Object) this;
        ProfileManager.getInstance().loadFromNbt(self, nbt);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        YWNAMod.LOGGER.info("[YWNA] Saving NBT data...");
        ServerPlayerEntity self = (ServerPlayerEntity) (Object) this;
        ProfileManager mgr = ProfileManager.getInstance();
        mgr.saveToNbt(self, nbt);
        if(mgr.isDisconnecting(self)) {
            mgr.unload(self);
            mgr.disconnecting.remove(self.getUuid());
            BehaviorTracker.unload(self);
        }
    }
}
