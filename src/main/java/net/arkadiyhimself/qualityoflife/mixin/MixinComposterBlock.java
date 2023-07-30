package net.arkadiyhimself.qualityoflife.mixin;

import net.arkadiyhimself.qualityoflife.Config.QoLconfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ComposterBlock.class)
public abstract class MixinComposterBlock {
    @Inject(at = @At("TAIL"), method = "extractProduce", cancellable = true)
    private static void rewardExp(BlockState blockState, Level level, BlockPos blockPos, CallbackInfoReturnable<BlockState> cir) {
        int exp = QoLconfig.EXP_FROM_COMPOSTER.get();
        if (!level.isClientSide() && exp != 0) {
            ExperienceOrb orb = new ExperienceOrb(level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), exp);
            level.addFreshEntity(orb);
        }
    }
}
