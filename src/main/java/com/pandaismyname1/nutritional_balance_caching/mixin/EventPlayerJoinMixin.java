package com.pandaismyname1.nutritional_balance_caching.mixin;

import com.dannyandson.nutritionalbalance.events.EventPlayerJoin;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EventPlayerJoin.class)
public class EventPlayerJoinMixin {

    @Inject(method = "EntityJoinWorldEvent", at = @At("HEAD"), cancellable = true, remap = false)
    private void onEntityJoinWorldEvent(EntityJoinLevelEvent event, CallbackInfo ci) {
        ci.cancel();
    }
}
