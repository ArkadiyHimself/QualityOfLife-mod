package net.arkadiyhimself.qualityoflife.mixin;

import net.arkadiyhimself.qualityoflife.Config.QoLconfig;
import net.minecraft.world.item.Tiers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Tiers.class)
public class MixinTiers {
    @Inject(at = @At(value = "TAIL"), method = "getUses", cancellable = true)
    private void changeValue(CallbackInfoReturnable<Integer> cir) {
        Tiers tier = (Tiers) (Object) this;
        int r = switch (tier) {
            case WOOD -> 64;
            case STONE -> 128;
            case IRON -> 256;
            case DIAMOND -> 1536;
            case GOLD -> 32;
            case NETHERITE -> 2048;
        };
        cir.setReturnValue(r);
    }
}
