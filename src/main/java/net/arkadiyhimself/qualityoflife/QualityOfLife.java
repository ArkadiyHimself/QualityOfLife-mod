package net.arkadiyhimself.qualityoflife;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import net.arkadiyhimself.qualityoflife.Config.QoLclient;
import net.arkadiyhimself.qualityoflife.Config.QoLconfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolderRegistry;
import org.slf4j.Logger;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.Iterator;

@Mod(QualityOfLife.MODID)
public class QualityOfLife
{
    public static final String MODID = "qualityoflife";
    private static final Logger LOGGER = LogUtils.getLogger();
    public QualityOfLife()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, QoLconfig.SPEC, "qualityoflife-common.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, QoLclient.SPEC, "qualityoflife-client.toml");
    }
    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            Iterator<Item> registry = ForgeRegistries.ITEMS.iterator();
            while (registry.hasNext()) {
                Item item = registry.next();
                boolean shulkerbox = item instanceof BlockItem blockItem && blockItem.getBlock() instanceof ShulkerBoxBlock;
                if (item instanceof PotionItem || item instanceof BedItem || item instanceof MinecartItem
                        || item instanceof RecordItem || shulkerbox || item instanceof EnchantedBookItem
                        || item instanceof DoubleHighBlockItem || item instanceof HorseArmorItem) {
                    try {
                        ObfuscationReflectionHelper.setPrivateValue(Item.class, item, 16, "f_41370_");
                    } catch (Exception ignored) {}
                }
            }
        });
        ComposterBlock.COMPOSTABLES.put(Items.ROTTEN_FLESH, 0.3F);
        ComposterBlock.COMPOSTABLES.put(Items.POISONOUS_POTATO, 1F);
    }
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
    }
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
        }
        @OnlyIn(Dist.CLIENT)
        @SubscribeEvent
        public static void renderSkullHearts(RegisterGuiOverlaysEvent event) {
        //    event.registerAboveAll("player_health", DoomedEffectStuff.DOOMED_HEARTS);
            event.registerAboveAll("offhand_durability", QualityOfLife.OFFHAND_DURABILITY);
        }
    }
    public static final IGuiOverlay OFFHAND_DURABILITY = (((gui, poseStack, partialTick, screenWidth, screenHeight) -> {
        if (Minecraft.getInstance().level != null) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (!player.isSpectator() && player.getOffhandItem().getMaxDamage() != 0) {
                int maxDur = player.getOffhandItem().getMaxDamage();
                int curDur = maxDur - player.getOffhandItem().getDamageValue();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                if (maxDur == curDur) {
                    Component durability = Component.translatable(Integer.toString(maxDur)).withStyle(ChatFormatting.BOLD);

                    gui.getFont().drawShadow(poseStack, durability, 94, 205, 16447222);
                } else {
                    Component durability1 = Component.translatable(curDur + "/").withStyle(ChatFormatting.BOLD);
                    Component durability2 = Component.translatable(String.valueOf(maxDur)).withStyle(ChatFormatting.BOLD);

                    gui.getFont().drawShadow(poseStack, durability1, 91, 200, 16447222);
                    gui.getFont().drawShadow(poseStack, durability2, 94, 210, 16447222);
                }
                RenderSystem.disableBlend();
            }
        }
    }));
}
