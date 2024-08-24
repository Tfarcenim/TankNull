package tfar.tanknull;

import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import tfar.tanknull.client.ModClient;
import tfar.tanknull.inventory.FluidListTooltip;

public class ModClientForge {

    static void setup(FMLClientSetupEvent event) {
        ModClient.setup();
    }

    public static void clientTooltip(RegisterClientTooltipComponentFactoriesEvent e) {
        e.register(FluidListTooltip.class, ModClient::tooltipImage);
    }

}
