package tfar.tanknull;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import tfar.tanknull.client.ModClient;
import tfar.tanknull.client.ModKeybinds;
import tfar.tanknull.inventory.FluidListTooltip;
import tfar.tanknull.network.server.C2SOpenMenuPacket;
import tfar.tanknull.platform.Services;

public class ModClientForge {

    static void setup(FMLClientSetupEvent event) {
        ModClient.setup();
        MinecraftForge.EVENT_BUS.addListener(ModClientForge::clientTick);
        MinecraftForge.EVENT_BUS.addListener(ModClientForge::onScroll);
        MinecraftForge.EVENT_BUS.addListener(ModClientForge::rightClick);
    }

    public static void rightClick(PlayerInteractEvent.RightClickItem event) {
        InteractionHand hand = event.getHand();
        Player player = event.getEntity();
        ItemStack stack = player.getItemInHand(hand);
        if (player.level().isClientSide && stack.getItem() instanceof TankItem && Screen.hasAltDown() && TankItem.getUseMode(stack)!= UseMode.bag) {
            Services.PLATFORM.sendToServer(new C2SOpenMenuPacket(hand));
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
        }
    }

    public static void clientTooltip(RegisterClientTooltipComponentFactoriesEvent e) {
        e.register(FluidListTooltip.class, ModClient::tooltipImage);
    }

    public static void keybinds(RegisterKeyMappingsEvent event) {
        event.register(ModKeybinds.CYCLE_USE_MODE);
    }

    public static void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            ModClient.keyPressed();
        }
    }

    public static void renderStack(RegisterGuiOverlaysEvent e) {
        e.registerBelow(VanillaGuiOverlay.CHAT_PANEL.id(), TankNull.MOD_ID,new TankHudOverlay());
    }

    public static void onScroll(InputEvent.MouseScrollingEvent e) {
        if (ModClient.onScroll(e.getScrollDelta()))e.setCanceled(true);
    }

}
