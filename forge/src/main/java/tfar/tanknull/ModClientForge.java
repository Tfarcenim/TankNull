package tfar.tanknull;

import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import tfar.tanknull.client.ModClient;
import tfar.tanknull.client.ModKeybinds;
import tfar.tanknull.inventory.FluidListTooltip;

public class ModClientForge {

    static void setup(FMLClientSetupEvent event) {
        ModClient.setup();
        MinecraftForge.EVENT_BUS.addListener(ModClientForge::clientTick);
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
}
