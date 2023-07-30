package net.arkadiyhimself.qualityoflife;

import com.mojang.blaze3d.vertex.PoseStack;
import net.arkadiyhimself.qualityoflife.Config.QoLclient;
import net.arkadiyhimself.qualityoflife.Config.QoLconfig;
import net.arkadiyhimself.qualityoflife.Helpers.UsefulMethods;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Objects;
import java.util.Random;

@Mod.EventBusSubscriber(modid = QualityOfLife.MODID)
public class QoLevents {
    private static int yOff = 0;
    private static int yOffDelay = 0;
    @SubscribeEvent
    public static void anvilTweaks(AnvilRepairEvent event) {
        event.setBreakChance((float) (double) QoLconfig.ANVIL_BREAK_CHANCE.get());
        event.getOutput().setRepairCost(event.getLeft().getBaseRepairCost());
    }
    @SubscribeEvent
    public static void rightClickInteractions(PlayerInteractEvent.RightClickBlock event) {
        Item item = event.getItemStack().getItem();
        BlockState block = event.getLevel().getBlockState(event.getHitVec().getBlockPos());
        Level level = event.getLevel();
        BlockPos pos = event.getPos();

        // cobbling blocks
        if (item instanceof PickaxeItem && QoLconfig.CAN_COBBLE_BLOCKS.get() && (event.getEntity().isCrouching() || !QoLconfig.SNEAK_TO_COBBLE.get())) {
            for (Block stone : UsefulMethods.COBBLES.keySet()) {
                if (block.is(stone)) {
                    event.getItemStack().hurtAndBreak(1, event.getEntity(), (durability) ->
                            durability.broadcastBreakEvent(event.getHand()));
                    level.setBlock(pos, UsefulMethods.COBBLES.get(stone).defaultBlockState(), 1);
                    double x = pos.getX();
                    double y = pos.getY();
                    double z = pos.getZ();
                    level.playLocalSound(x, y, z, block.getBlock().getSoundType(block, event.getLevel(), pos, null).getBreakSound(), SoundSource.BLOCKS, 1F, 1F, true);
                    event.getEntity().swing(event.getHand());
                    int amount = switch (Minecraft.getInstance().options.particles().get()) {
                        case MINIMAL -> 15;
                        case DECREASED -> 25;
                        case ALL -> 35;
                    };
                    Random random = new Random();
                    for (int i = 0; i <= amount; i++) {
                        Vec3 coords = UsefulMethods.geometry.randomSidesOfSquare(x + 0.5D, y + 0.5D, z + 0.5D, 0.5D);
                        assert Minecraft.getInstance().level != null;
                        Minecraft.getInstance().particleEngine.add(new TerrainParticle(Minecraft.getInstance().level, coords.x, coords.y, coords.z,
                                0, 0, 0, block));
                    }
                    break;
                }
            }
        }
        // bonemealing more stuff
        if (item instanceof BoneMealItem && QoLconfig.BONEMEAL_MORE.get()) {
            if (block.getBlock() instanceof CactusBlock || block.getBlock() instanceof SugarCaneBlock) {
                BlockPos topMostPos = UsefulMethods.highestGrow(level, pos, block.getBlock(), true);
                BlockState topMostState = level.getBlockState(topMostPos);

                if (topMostState.hasProperty(BlockStateProperties.AGE_15) && level.isEmptyBlock(topMostPos.above())) {
                    int age = topMostState.getValue(BlockStateProperties.AGE_15);

                    int plantHeight;
                    for (plantHeight = 1; level.getBlockState(topMostPos.below(plantHeight)).is(block.getBlock()); ++plantHeight) {

                    }

                    if (plantHeight >= 3)
                        return;

                    if (!level.isClientSide) {
                        level.levelEvent(2005, pos, 0);
                    }

                    age += level.random.nextInt(10);
                    level.setBlock(topMostPos, topMostState.setValue(BlockStateProperties.AGE_15, Math.min(age, 15)), 4);

                    if (level instanceof ServerLevel) {
                        level.getBlockState(topMostPos).randomTick((ServerLevel) level, topMostPos, level.random);
                    }
                }
                event.getItemStack().shrink(1);
                event.getEntity().swing(event.getHand());
            } else if (block.getBlock() instanceof VineBlock) {
                if (!block.isRandomlyTicking()) {
                    return;
                }
                if (level.isClientSide) {
                    UsefulMethods.spawnBonemealParticles(level, pos);
                }
                int cycles = 7 + level.random.nextInt(7);

                if (level instanceof ServerLevel) {
                    for (int i = 0; i <= cycles; i++) {
                        block.randomTick((ServerLevel) level, pos, level.random);
                    }

                    block.updateNeighbourShapes(level, pos, 4);
                }
                event.getItemStack().shrink(1);
                event.getEntity().swing(event.getHand());
            } else if (block.getBlock() instanceof NetherWartBlock) {
                if (!block.isRandomlyTicking()) {
                    return;
                }

                if (!level.isClientSide) {
                    level.levelEvent(2005, pos, 0);
                }

                int cycles = 1 + level.random.nextInt(1);
                cycles *= 11;

                if (level instanceof ServerLevel) {
                    for (int i = 0; i <= cycles; i++) {
                        block.randomTick((ServerLevel) level, pos, level.random);
                    }
                }
                event.getItemStack().shrink(1);
                event.getEntity().swing(event.getHand());
            }
        }
    }
    @SubscribeEvent
    public static void anvilCosts(AnvilUpdateEvent event) {
        int finalcost = 0;
        int nextLevel;
        ItemStack first = event.getLeft().copy();
        ItemStack second = event.getRight().copy();

        if (first.is(Items.ENCHANTED_BOOK) && second.is(Items.ENCHANTED_BOOK) && QoLconfig.EASY_BOOK_COMBINING.get() &&
                EnchantmentHelper.getEnchantments(first).size() == 1 && EnchantmentHelper.getEnchantments(second).size() == 1) {
            ItemStack result = first.copy();
            Map<Enchantment, Integer> ench1 = EnchantmentHelper.getEnchantments(first);
            Map<Enchantment, Integer> ench2 = EnchantmentHelper.getEnchantments(second);
            if (!Objects.equals(ench1.keySet(), ench2.keySet())) { return; }
            for (Enchantment enchantment : ench1.keySet()) {
                if (enchantment != null) {
                    int level1 = ench1.get(enchantment);
                    int level2 = ench2.getOrDefault(enchantment,0);
                    nextLevel = level1 == level2 ? level1 + 1 : Math.max(level1, level2);
                    nextLevel = Math.min(nextLevel, enchantment.getMaxLevel());
                    ench1.put(enchantment, nextLevel);
                    finalcost += nextLevel;
                }
            }
            if (finalcost != 0) {
                EnchantmentHelper.setEnchantments(ench1, result);
                event.setCost(finalcost);
                event.setOutput(result);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void seeDurability(ItemTooltipEvent event) {
        if (event.getItemStack().isEmpty() || event.getItemStack().getMaxDamage() == 0 || !QoLclient.INVENTORY_DURABILITY.get()) { return; }
        int maxDur = event.getItemStack().getMaxDamage();
        int curDur = maxDur - event.getItemStack().getDamageValue();
        Component cur = Component.literal(Integer.toString(curDur));
        Component max = Component.literal(Integer.toString(maxDur));
        MutableComponent nums = curDur == maxDur ? Component.translatable("qualityoflife.durability.full", max).withStyle(ChatFormatting.BOLD)
                : Component.translatable("qualityoflife.durability.notfull", cur, max).withStyle(ChatFormatting.BOLD);
        event.getToolTip().add(nums);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void showDurability(RenderGuiOverlayEvent.Pre event) {
        if (event.getOverlay() == VanillaGuiOverlay.ITEM_NAME.type() && QoLclient.HOTBAR_DURABILITY.get()) {
            Minecraft mc = Minecraft.getInstance();
            ItemStack item = mc.player.getMainHandItem();
            if (item.isEmpty() || item.getMaxDamage() == 0) { return; }
            int maxDur = item.getMaxDamage();
            int curDur = maxDur - item.getDamageValue();
            Component durability = maxDur == curDur ? Component.translatable(Integer.toString(maxDur)).withStyle(ChatFormatting.BOLD)
                    : Component.translatable(curDur + "/" + maxDur).withStyle(ChatFormatting.BOLD);
            int x = (event.getWindow().getGuiScaledWidth() - mc.font.width(durability)) / 2;
            int y = event.getWindow().getGuiScaledHeight();
            int i = 0;
            if (Minecraft.getInstance().player.isCreative()) {
                mc.font.drawShadow(event.getPoseStack(), durability, x, y - 34, 16447222);
            } else {
                mc.font.drawShadow(event.getPoseStack(), durability, x, y - 58 + yOff, 16447222);
            }
        }
    }
    @SubscribeEvent
    public static void clientChanges(TickEvent event) {
        if (event.type == TickEvent.Type.CLIENT) {
            yOffDelay = Math.max(0, yOffDelay - 1);
            if (yOffDelay == 0) { yOff = Math.min(0, yOff + 1); }
        }
    }
    @SubscribeEvent
    public static void hotbarChanged(LivingEquipmentChangeEvent event) {
        if (event.getSlot() == EquipmentSlot.MAINHAND) {
            yOff = -10;
            yOffDelay = 75;
        }
    }
    @SubscribeEvent
    public static void sneakingForStripping(BlockEvent.BlockToolModificationEvent event) {
        ToolAction action = event.getToolAction();
        if (action == ToolActions.AXE_STRIP || action == ToolActions.AXE_SCRAPE || action == ToolActions.AXE_WAX_OFF) {
            event.setCanceled(!event.getPlayer().isCrouching() && QoLconfig.SNEAK_TO_USE_AXE.get());
        }
    }
}
