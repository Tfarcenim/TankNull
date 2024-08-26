package tfar.tanknull;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import tfar.tanknull.client.ModClient;

public class TankHudOverlay implements IGuiOverlay {
    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        ModClient.render(gui,guiGraphics,partialTick,screenWidth,screenHeight);
    }
}
