package tfar.tanknull.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import tfar.tanknull.init.ModMenuTypes;

public class ModClient {

    public static ClientTooltipComponent tooltipImage(TooltipComponent data) {
      //  if (data instanceof DankTooltip dankTooltip) {
       //     return new ClientDankTooltip(dankTooltip);
     //   }
        return null;
    }

    public static Player getLocalPlayer() {
        return Minecraft.getInstance().player;
    }

    public static void setup() {
        MenuScreens.register(ModMenuTypes.TANK_1, TankScreen::t1);
        MenuScreens.register(ModMenuTypes.TANK_2, TankScreen::t2);
        MenuScreens.register(ModMenuTypes.TANK_3, TankScreen::t3);
        MenuScreens.register(ModMenuTypes.TANK_4, TankScreen::t4);
        MenuScreens.register(ModMenuTypes.TANK_5, TankScreen::t5);
        MenuScreens.register(ModMenuTypes.TANK_6, TankScreen::t6);
        MenuScreens.register(ModMenuTypes.TANK_7, TankScreen::t7);

        //MenuScreens.register(ModMenuTypes.change_frequency, ChangeFrequencyScreen::new);
    }
}
