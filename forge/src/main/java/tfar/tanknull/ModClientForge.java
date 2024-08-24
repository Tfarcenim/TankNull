package tfar.tanknull;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import tfar.tanknull.client.ModClient;

public class ModClientForge {

    static void setup(FMLClientSetupEvent event) {
        ModClient.setup();
    }

}
