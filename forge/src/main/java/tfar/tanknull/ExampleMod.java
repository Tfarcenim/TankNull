package tfar.tanknull;

import net.minecraftforge.fml.common.Mod;

@Mod(TankNull.MOD_ID)
public class ExampleMod {
    
    public ExampleMod() {
    
        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.
    
        // Use Forge to bootstrap the Common mod.
        TankNull.LOG.info("Hello Forge world!");
        TankNull.init();
        
    }
}